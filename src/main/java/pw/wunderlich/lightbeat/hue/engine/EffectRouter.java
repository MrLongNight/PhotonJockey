package pw.wunderlich.lightbeat.hue.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Routes effect frames to appropriate controllers based on light configuration.
 * Loads lightmap.json to determine which lights use fast (UDP) vs low (HTTP) control.
 */
public class EffectRouter {

    private static final Logger LOG = LoggerFactory.getLogger(EffectRouter.class);

    private final Map<String, ControlType> lightControlMap;
    private IFastEffectController fastController;
    private ILowEffectController lowController;

    /**
     * Control type enumeration.
     */
    public enum ControlType {
        FAST_UDP,
        LOW_HTTP
    }

    /**
     * Create an EffectRouter with an empty light map.
     */
    public EffectRouter() {
        this.lightControlMap = new HashMap<>();
    }

    /**
     * Load light map configuration from a JSON file.
     *
     * @param configPath path to the lightmap.json file
     * @throws IOException if the file cannot be read or parsed
     */
    public void loadLightMap(Path configPath) throws IOException {
        if (!Files.exists(configPath)) {
            throw new IOException("Configuration file not found: " + configPath);
        }

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            loadLightMapFromStream(inputStream);
        }
    }

    /**
     * Load light map configuration from an input stream.
     *
     * @param inputStream input stream containing the lightmap JSON
     * @throws IOException if the stream cannot be read or parsed
     */
    public void loadLightMapFromStream(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(inputStream);

        lightControlMap.clear();

        if (root.has("lights")) {
            JsonNode lights = root.get("lights");
            for (JsonNode light : lights) {
                String id = light.get("id").asText();
                String controlTypeStr = light.get("controlType").asText();
                
                try {
                    ControlType controlType = ControlType.valueOf(controlTypeStr);
                    lightControlMap.put(id, controlType);
                    LOG.debug("Loaded light {} with control type {}", id, controlType);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Invalid control type '{}' for light '{}', skipping", controlTypeStr, id);
                }
            }
        }

        LOG.info("Loaded {} lights from configuration", lightControlMap.size());
    }

    /**
     * Set the fast effect controller.
     *
     * @param controller the fast effect controller to use
     */
    public void setFastController(IFastEffectController controller) {
        this.fastController = controller;
    }

    /**
     * Set the low effect controller.
     *
     * @param controller the low effect controller to use
     */
    public void setLowController(ILowEffectController controller) {
        this.lowController = controller;
    }

    /**
     * Route an effect frame to the appropriate controllers.
     * Splits updates between fast and low controllers based on light configuration.
     *
     * @param frame the effect frame to route
     */
    public void routeFrame(EffectFrame frame) {
        if (frame == null || frame.getUpdates() == null) {
            LOG.warn("Received null frame or updates, ignoring");
            return;
        }

        List<LightUpdateDTO> fastUpdates = new ArrayList<>();
        List<LightUpdateDTO> lowUpdates = new ArrayList<>();

        for (LightUpdateDTO update : frame.getUpdates()) {
            ControlType controlType = lightControlMap.get(update.getLightId());

            if (controlType == null) {
                LOG.warn("Unknown light ID: {}, skipping", update.getLightId());
                continue;
            }

            if (controlType == ControlType.FAST_UDP) {
                fastUpdates.add(update);
            } else {
                lowUpdates.add(update);
            }
        }

        // Send to fast controller if we have fast updates
        if (!fastUpdates.isEmpty() && fastController != null) {
            EffectFrame fastFrame = new EffectFrame(fastUpdates, frame.getTimestamp());
            fastController.sendFrame(fastFrame);
            LOG.debug("Routed {} updates to fast controller", fastUpdates.size());
        }

        // Send to low controller if we have low updates
        if (!lowUpdates.isEmpty() && lowController != null) {
            lowController.updateLights(lowUpdates);
            LOG.debug("Routed {} updates to low controller", lowUpdates.size());
        }
    }

    /**
     * Get the control type for a specific light.
     *
     * @param lightId the light ID
     * @return the control type, or null if the light is not configured
     */
    public ControlType getControlType(String lightId) {
        return lightControlMap.get(lightId);
    }

    /**
     * Get the number of configured lights.
     *
     * @return number of lights in the configuration
     */
    public int getLightCount() {
        return lightControlMap.size();
    }
}
