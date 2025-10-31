package io.github.mrlongnight.photonjockey.hue.engine;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.mrlongnight.photonjockey.hue.engine.LightUpdateDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for LowEffectController with mock HTTP server.
 */
class LowEffectControllerTest {

    private HttpServer mockServer;
    private LowEffectController controller;
    private int serverPort;
    private AtomicInteger requestCount;
    private List<String> receivedRequests;
    private volatile int nextResponseCode;
    private volatile String retryAfterHeader;

    @BeforeEach
    void setUp() throws IOException {
        // Start mock HTTP server
        serverPort = 8889;
        mockServer = HttpServer.create(new InetSocketAddress("localhost", serverPort), 0);
        requestCount = new AtomicInteger(0);
        receivedRequests = new ArrayList<>();
        nextResponseCode = 200;
        retryAfterHeader = null;

        mockServer.createContext("/api", exchange -> {
            requestCount.incrementAndGet();
            
            // Read request body
            String body = new String(exchange.getRequestBody().readAllBytes());
            receivedRequests.add(body);
            
            // Send response
            int responseCode = nextResponseCode;
            byte[] response = "[{\"success\":{}}]".getBytes();
            
            if (retryAfterHeader != null) {
                exchange.getResponseHeaders().add("Retry-After", retryAfterHeader);
            }
            
            exchange.sendResponseHeaders(responseCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        
        mockServer.setExecutor(null);
        mockServer.start();
        
        // Create controller pointing to mock server
        controller = new LowEffectController(
                "localhost:" + serverPort,
                "test-api-key",
                100, // 100ms batch window
                10.0 // 10 requests per second
        );
    }

    @AfterEach
    void tearDown() {
        if (controller != null && controller.isRunning()) {
            controller.stop();
        }
        if (mockServer != null) {
            mockServer.stop(0);
        }
    }

    @Test
    void testConstructor() {
        assertFalse(controller.isRunning());
        assertEquals("localhost:" + serverPort, controller.getBridgeIp());
        assertEquals(100, controller.getBatchWindowMs());
        assertEquals(10.0, controller.getRequestsPerSecond());
    }

    @Test
    void testStartAndStop() {
        controller.start();
        assertTrue(controller.isRunning());
        
        controller.stop();
        assertFalse(controller.isRunning());
    }

    @Test
    void testStartWhenAlreadyRunning() {
        controller.start();
        assertTrue(controller.isRunning());
        
        // Starting again should not cause issues
        controller.start();
        assertTrue(controller.isRunning());
        
        controller.stop();
    }

    @Test
    void testUpdateLightsAddsToQueue() {
        LightUpdateDTO update = new LightUpdateDTO("1", 200, 0.5, 0.8, 0);
        List<LightUpdateDTO> updates = List.of(update);
        
        controller.updateLights(updates);
        
        assertEquals(1, controller.getPendingUpdateCount());
    }

    @Test
    void testUpdateLightsWithNullList() {
        controller.updateLights(null);
        assertEquals(0, controller.getPendingUpdateCount());
    }

    @Test
    void testUpdateLightsWithEmptyList() {
        controller.updateLights(List.of());
        assertEquals(0, controller.getPendingUpdateCount());
    }

    @Test
    void testBatchProcessing() throws InterruptedException {
        controller.start();
        
        // Add updates
        LightUpdateDTO update1 = new LightUpdateDTO("1", 200, null, null, null);
        LightUpdateDTO update2 = new LightUpdateDTO("2", 150, null, null, null);
        controller.updateLights(List.of(update1, update2));
        
        // Wait for batch to be processed
        Thread.sleep(300);
        
        // Should have sent 2 requests
        assertTrue(requestCount.get() >= 2, "Expected at least 2 requests, got " 
                + requestCount.get());
    }

    @Test
    void testRateLimiting() throws InterruptedException {
        // Create controller with 2 requests per second limit
        LowEffectController slowController = new LowEffectController(
                "localhost:" + serverPort,
                "test-api-key",
                50, // 50ms batch window
                2.0 // 2 requests per second
        );
        
        try {
            slowController.start();
            
            // Add 5 updates
            List<LightUpdateDTO> updates = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                updates.add(new LightUpdateDTO(String.valueOf(i), 200, null, null, null));
            }
            slowController.updateLights(updates);
            
            // Wait for first batch
            Thread.sleep(200);
            
            long startTime = System.currentTimeMillis();
            int initialCount = requestCount.get();
            
            // Wait up to 3 seconds for all requests
            Thread.sleep(3000);
            
            long elapsedTime = System.currentTimeMillis() - startTime;
            int finalCount = requestCount.get();
            int requestsSent = finalCount - initialCount;
            
            // With 2 req/s limit, we shouldn't send all 5 requests in under 2 seconds
            double actualRate = (requestsSent * 1000.0) / elapsedTime;
            assertTrue(actualRate <= 3.0, "Rate should be ~2 req/s, got " + actualRate);
            
        } finally {
            slowController.stop();
        }
    }

    @Test
    void testHttp429Handling() throws InterruptedException {
        controller.start();
        
        // Set server to return 429
        nextResponseCode = 429;
        retryAfterHeader = "1"; // 1 second
        
        LightUpdateDTO update = new LightUpdateDTO("1", 200, null, null, null);
        controller.updateLights(List.of(update));
        
        // Wait for first batch
        Thread.sleep(300);
        
        int firstRequestCount = requestCount.get();
        assertTrue(firstRequestCount >= 1, "Should have sent at least 1 request");
        
        // Add another update for same light immediately
        nextResponseCode = 200; // Set to success for next attempt
        retryAfterHeader = null;
        controller.updateLights(List.of(update));
        
        // Wait a bit - should be in backoff
        Thread.sleep(300);
        
        int secondRequestCount = requestCount.get();
        // Should not have sent another request yet due to backoff
        assertEquals(firstRequestCount, secondRequestCount, 
                "Should be in backoff, not sending requests");
        
        // Wait for backoff to expire
        Thread.sleep(1000);
        
        // Add another update
        controller.updateLights(List.of(update));
        
        // Wait for batch
        Thread.sleep(300);
        
        int thirdRequestCount = requestCount.get();
        assertTrue(thirdRequestCount > secondRequestCount, 
                "Should have sent request after backoff expired");
    }

    @Test
    void testHttp429WithoutRetryAfterHeader() throws InterruptedException {
        controller.start();
        
        // Set server to return 429 without Retry-After header
        nextResponseCode = 429;
        
        LightUpdateDTO update = new LightUpdateDTO("1", 200, null, null, null);
        controller.updateLights(List.of(update));
        
        // Wait for first batch
        Thread.sleep(300);
        
        int firstRequestCount = requestCount.get();
        assertTrue(firstRequestCount >= 1);
        
        // Immediately add another update
        nextResponseCode = 200;
        controller.updateLights(List.of(update));
        
        // Wait a bit - should be in backoff
        Thread.sleep(300);
        
        int secondRequestCount = requestCount.get();
        // Should be in backoff
        assertEquals(firstRequestCount, secondRequestCount);
    }

    @Test
    void testJsonPayloadWithAllFields() throws InterruptedException {
        controller.start();
        
        LightUpdateDTO update = new LightUpdateDTO("1", 200, 0.5, 0.8, 5);
        controller.updateLights(List.of(update));
        
        // Wait for batch
        Thread.sleep(300);
        
        assertTrue(requestCount.get() >= 1);
        assertFalse(receivedRequests.isEmpty());
        
        String json = receivedRequests.get(0);
        assertTrue(json.contains("\"bri\":200"));
        assertTrue(json.contains("\"hue\":32767")); // 0.5 * 65535
        assertTrue(json.contains("\"sat\":203")); // 0.8 * 254
        assertTrue(json.contains("\"transitiontime\":5"));
    }

    @Test
    void testJsonPayloadWithBrightnessOnly() throws InterruptedException {
        controller.start();
        
        LightUpdateDTO update = new LightUpdateDTO("1", 200, null, null, null);
        controller.updateLights(List.of(update));
        
        // Wait for batch
        Thread.sleep(300);
        
        assertTrue(requestCount.get() >= 1);
        assertFalse(receivedRequests.isEmpty());
        
        String json = receivedRequests.get(0);
        assertTrue(json.contains("\"bri\":200"));
        assertFalse(json.contains("\"hue\""));
        assertFalse(json.contains("\"sat\""));
        assertFalse(json.contains("\"transitiontime\""));
    }

    @Test
    void testMultipleLightsInBatch() throws InterruptedException {
        controller.start();
        
        List<LightUpdateDTO> updates = new ArrayList<>();
        updates.add(new LightUpdateDTO("1", 200, null, null, null));
        updates.add(new LightUpdateDTO("2", 150, null, null, null));
        updates.add(new LightUpdateDTO("3", 100, null, null, null));
        
        controller.updateLights(updates);
        
        // Wait for batch + rate limiting (100ms batch + 3 requests * 100ms rate limit = ~400ms)
        Thread.sleep(500);
        
        // Should have sent 3 requests (one per light)
        assertTrue(requestCount.get() >= 3);
    }

    @Test
    void testBackoffDoesNotAffectOtherLights() throws InterruptedException {
        controller.start();
        
        // Set server to return 429
        nextResponseCode = 429;
        retryAfterHeader = "2"; // 2 seconds
        
        // Send update for light 1 - will get 429
        LightUpdateDTO update1 = new LightUpdateDTO("1", 200, null, null, null);
        controller.updateLights(List.of(update1));
        
        // Wait for batch
        Thread.sleep(300);
        
        int countAfterFirst = requestCount.get();
        
        // Now set server to return 200 for subsequent requests
        nextResponseCode = 200;
        retryAfterHeader = null;
        
        // Send update for light 2 - should succeed
        LightUpdateDTO update2 = new LightUpdateDTO("2", 150, null, null, null);
        controller.updateLights(List.of(update2));
        
        // Wait for batch
        Thread.sleep(300);
        
        int countAfterSecond = requestCount.get();
        
        // Light 2 should have been processed
        assertTrue(countAfterSecond > countAfterFirst, 
                "Light 2 should not be affected by light 1's backoff");
    }

    @Test
    void testConcurrentUpdates() throws InterruptedException {
        controller.start();
        
        CountDownLatch latch = new CountDownLatch(3);
        
        // Submit updates from multiple threads
        for (int i = 0; i < 3; i++) {
            final int lightId = i + 1;
            new Thread(() -> {
                LightUpdateDTO update = new LightUpdateDTO(
                        String.valueOf(lightId), 200, null, null, null);
                controller.updateLights(List.of(update));
                latch.countDown();
            }).start();
        }
        
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        
        // Wait for batch processing + rate limiting (100ms batch + 3 requests * 100ms = ~400ms)
        Thread.sleep(500);
        
        // All 3 updates should have been processed
        assertTrue(requestCount.get() >= 3);
    }

    @Test
    void testStopClearsScheduler() throws InterruptedException {
        controller.start();
        
        LightUpdateDTO update = new LightUpdateDTO("1", 200, null, null, null);
        controller.updateLights(List.of(update));
        
        // Wait for batch
        Thread.sleep(300);
        
        controller.stop();
        
        int countAfterStop = requestCount.get();
        
        // Add more updates after stop
        controller.updateLights(List.of(update));
        
        // Wait
        Thread.sleep(300);
        
        // No new requests should be sent
        assertEquals(countAfterStop, requestCount.get());
    }
}
