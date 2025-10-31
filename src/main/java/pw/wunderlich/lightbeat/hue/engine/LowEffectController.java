package pw.wunderlich.lightbeat.hue.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * HTTP-based controller for low-frequency light updates.
 * Implements batching, rate limiting, and backoff for HTTP 429 responses.
 */
public class LowEffectController implements ILowEffectController {

    private static final Logger LOG = LoggerFactory.getLogger(LowEffectController.class);

    private final String bridgeIp;
    private final String apiKey;
    private final HttpClient httpClient;
    private final ScheduledExecutorService scheduler;
    private final long batchWindowMs;
    private final double requestsPerSecond;
    private final List<LightUpdateDTO> pendingUpdates;
    private final Map<String, Long> lightBackoffUntil;
    private final Map<String, Long> lightBackoffDuration;
    private final AtomicLong lastRequestTime;
    private final long minRequestIntervalMs;
    private volatile boolean isRunning;

    /**
     * Create a new LowEffectController.
     *
     * @param bridgeIp IP address of the Hue bridge
     * @param apiKey API key for authentication
     * @param batchWindowMs batching window in milliseconds (e.g., 100)
     * @param requestsPerSecond rate limit in requests per second (e.g., 10)
     */
    public LowEffectController(String bridgeIp, String apiKey, long batchWindowMs, 
                               double requestsPerSecond) {
        this.bridgeIp = bridgeIp;
        this.apiKey = apiKey;
        this.batchWindowMs = batchWindowMs;
        this.requestsPerSecond = requestsPerSecond;
        this.minRequestIntervalMs = (long) (1000.0 / requestsPerSecond);
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "LowEffectController-Scheduler");
            t.setDaemon(true);
            return t;
        });
        
        this.pendingUpdates = new ArrayList<>();
        this.lightBackoffUntil = new ConcurrentHashMap<>();
        this.lightBackoffDuration = new ConcurrentHashMap<>();
        this.lastRequestTime = new AtomicLong(0);
        this.isRunning = false;
    }

    /**
     * Start the controller and begin processing batched updates.
     */
    public void start() {
        if (isRunning) {
            LOG.warn("LowEffectController already running");
            return;
        }
        
        isRunning = true;
        scheduler.scheduleAtFixedRate(
                this::processBatch,
                batchWindowMs,
                batchWindowMs,
                TimeUnit.MILLISECONDS
        );
        LOG.info("LowEffectController started with batch window {}ms and rate limit {} req/s",
                batchWindowMs, requestsPerSecond);
    }

    /**
     * Stop the controller and release resources.
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        
        isRunning = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOG.info("LowEffectController stopped");
    }

    @Override
    public void updateLights(List<LightUpdateDTO> updates) {
        if (updates == null || updates.isEmpty()) {
            return;
        }
        
        synchronized (pendingUpdates) {
            pendingUpdates.addAll(updates);
        }
        
        LOG.debug("Added {} updates to pending queue", updates.size());
    }

    /**
     * Process the current batch of pending updates.
     */
    private void processBatch() {
        List<LightUpdateDTO> batch;
        
        synchronized (pendingUpdates) {
            if (pendingUpdates.isEmpty()) {
                return;
            }
            batch = new ArrayList<>(pendingUpdates);
            pendingUpdates.clear();
        }
        
        LOG.debug("Processing batch of {} updates", batch.size());
        
        long now = System.currentTimeMillis();
        
        // Filter out lights that are in backoff
        List<LightUpdateDTO> filteredBatch = new ArrayList<>();
        for (LightUpdateDTO update : batch) {
            Long backoffUntil = lightBackoffUntil.get(update.getLightId());
            if (backoffUntil == null || now >= backoffUntil) {
                filteredBatch.add(update);
            } else {
                LOG.debug("Light {} is in backoff until {}", update.getLightId(), backoffUntil);
            }
        }
        
        if (filteredBatch.isEmpty()) {
            return;
        }
        
        // Apply rate limiting
        long timeSinceLastRequest = now - lastRequestTime.get();
        if (timeSinceLastRequest < minRequestIntervalMs) {
            long sleepTime = minRequestIntervalMs - timeSinceLastRequest;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        // Send updates
        for (LightUpdateDTO update : filteredBatch) {
            sendUpdate(update);
        }
    }

    /**
     * Send a single light update via HTTP.
     *
     * @param update the light update to send
     */
    private void sendUpdate(LightUpdateDTO update) {
        try {
            String url = String.format("http://%s/api/%s/lights/%s/state",
                    bridgeIp, apiKey, update.getLightId());
            
            String json = buildJsonPayload(update);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            
            lastRequestTime.set(System.currentTimeMillis());
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            
            if (statusCode == 429) {
                handleRateLimitExceeded(update.getLightId(), response);
            } else if (statusCode >= 200 && statusCode < 300) {
                LOG.debug("Successfully updated light {}", update.getLightId());
                lightBackoffUntil.remove(update.getLightId());
                lightBackoffDuration.remove(update.getLightId());
            } else {
                LOG.warn("HTTP request failed with status {} for light {}: {}",
                        statusCode, update.getLightId(), response.body());
            }
            
        } catch (Exception e) {
            LOG.error("Failed to send update for light {}: {}", 
                    update.getLightId(), e.getMessage());
        }
    }

    /**
     * Handle HTTP 429 (Too Many Requests) response with exponential backoff.
     *
     * @param lightId the light ID
     * @param response the HTTP response
     */
    private void handleRateLimitExceeded(String lightId, HttpResponse<String> response) {
        // Try to get Retry-After header
        long backoffMs = 1000; // Default 1 second
        boolean useExponentialBackoff = false;
        
        if (response.headers().firstValue("Retry-After").isPresent()) {
            try {
                int retryAfter = Integer.parseInt(
                        response.headers().firstValue("Retry-After").get());
                backoffMs = retryAfter * 1000L;
            } catch (NumberFormatException e) {
                LOG.warn("Invalid Retry-After header value");
                useExponentialBackoff = true;
            }
        } else {
            useExponentialBackoff = true;
        }
        
        if (useExponentialBackoff) {
            // Use exponential backoff based on previous backoff duration
            Long previousBackoffMs = lightBackoffDuration.get(lightId);
            if (previousBackoffMs != null && previousBackoffMs < 10000) {
                // Double the previous backoff, up to max 10 seconds
                backoffMs = Math.min(previousBackoffMs * 2, 10000);
            }
            // Store the backoff duration for next time
            lightBackoffDuration.put(lightId, backoffMs);
        }
        
        long backoffUntil = System.currentTimeMillis() + backoffMs;
        lightBackoffUntil.put(lightId, backoffUntil);
        
        LOG.warn("HTTP 429 for light {}, backing off for {}ms", lightId, backoffMs);
    }

    /**
     * Build JSON payload for a light update.
     *
     * @param update the light update
     * @return JSON string
     */
    private String buildJsonPayload(LightUpdateDTO update) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        if (update.getBrightness() != null) {
            json.append("\"bri\":").append(update.getBrightness());
            first = false;
        }
        
        if (update.getHue() != null) {
            if (!first) {
                json.append(",");
            }
            // Convert 0.0-1.0 to 0-65535
            int hueValue = (int) (update.getHue() * 65535);
            json.append("\"hue\":").append(hueValue);
            first = false;
        }
        
        if (update.getSaturation() != null) {
            if (!first) {
                json.append(",");
            }
            // Convert 0.0-1.0 to 0-254
            int satValue = (int) (update.getSaturation() * 254);
            json.append("\"sat\":").append(satValue);
            first = false;
        }
        
        if (update.getTransitionTime() != null) {
            if (!first) {
                json.append(",");
            }
            json.append("\"transitiontime\":").append(update.getTransitionTime());
        }
        
        json.append("}");
        return json.toString();
    }

    /**
     * Check if the controller is running.
     *
     * @return true if running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Get the number of pending updates.
     *
     * @return number of pending updates
     */
    public int getPendingUpdateCount() {
        synchronized (pendingUpdates) {
            return pendingUpdates.size();
        }
    }

    /**
     * Get the bridge IP address.
     *
     * @return bridge IP
     */
    public String getBridgeIp() {
        return bridgeIp;
    }

    /**
     * Get the batch window in milliseconds.
     *
     * @return batch window
     */
    public long getBatchWindowMs() {
        return batchWindowMs;
    }

    /**
     * Get the rate limit in requests per second.
     *
     * @return requests per second
     */
    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }
}
