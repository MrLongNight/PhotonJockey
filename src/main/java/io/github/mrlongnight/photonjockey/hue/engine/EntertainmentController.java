package io.github.mrlongnight.photonjockey.hue.engine;

import io.github.mrlongnight.photonjockey.hue.dto.EntertainmentGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for managing entertainment mode state.
 * Handles activation/deactivation of entertainment mode and tracks which lights
 * are currently in entertainment mode (and thus cannot be controlled via slow HTTPS).
 */
public class EntertainmentController {

    private static final Logger LOG = LoggerFactory.getLogger(EntertainmentController.class);

    private final AtomicBoolean entertainmentModeActive = new AtomicBoolean(false);
    private EntertainmentGroupInfo activeGroup;
    private final Set<String> entertainmentLightIds = new HashSet<>();
    private IFastEffectController fastEffectController;

    /**
     * Check if entertainment mode is currently active.
     *
     * @return true if entertainment mode is active
     */
    public boolean isEntertainmentModeActive() {
        return entertainmentModeActive.get();
    }

    /**
     * Get the currently active entertainment group.
     *
     * @return the active entertainment group, or null if none is active
     */
    public EntertainmentGroupInfo getActiveGroup() {
        return activeGroup;
    }

    /**
     * Check if a specific light is currently in entertainment mode.
     * Lights in entertainment mode should not be controlled via slow HTTPS effects.
     *
     * @param lightId the ID of the light to check
     * @return true if the light is in entertainment mode
     */
    public boolean isLightInEntertainmentMode(String lightId) {
        return entertainmentModeActive.get() && entertainmentLightIds.contains(lightId);
    }

    /**
     * Activate entertainment mode for a specific entertainment group.
     * This will start the fast effect controller and mark all lights in the group
     * as unavailable for slow HTTPS control.
     *
     * @param groupInfo the entertainment group to activate
     * @param controller the fast effect controller to use for sending frames
     * @throws IllegalStateException if entertainment mode is already active
     */
    public void activateEntertainmentMode(EntertainmentGroupInfo groupInfo, IFastEffectController controller) {
        if (entertainmentModeActive.get()) {
            throw new IllegalStateException("Entertainment mode is already active");
        }

        if (groupInfo == null) {
            throw new IllegalArgumentException("Group info cannot be null");
        }

        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }

        LOG.info("Activating entertainment mode for group: {}", groupInfo.getName());

        this.activeGroup = groupInfo;
        this.fastEffectController = controller;

        // Track all light IDs in this entertainment group
        entertainmentLightIds.clear();
        groupInfo.getLights().forEach(light -> entertainmentLightIds.add(light.getId()));

        // Start the fast effect session
        fastEffectController.startSession();

        entertainmentModeActive.set(true);
        LOG.info("Entertainment mode activated with {} lights", entertainmentLightIds.size());
    }

    /**
     * Deactivate entertainment mode.
     * This will stop the fast effect controller and make all lights available
     * for slow HTTPS control again.
     */
    public void deactivateEntertainmentMode() {
        if (!entertainmentModeActive.get()) {
            LOG.debug("Entertainment mode is not active, nothing to deactivate");
            return;
        }

        LOG.info("Deactivating entertainment mode for group: {}", activeGroup != null ? activeGroup.getName() : "unknown");

        // Stop the fast effect session
        if (fastEffectController != null) {
            fastEffectController.stopSession();
            fastEffectController = null;
        }

        entertainmentLightIds.clear();
        activeGroup = null;
        entertainmentModeActive.set(false);

        LOG.info("Entertainment mode deactivated");
    }

    /**
     * Send a frame to the currently active entertainment group.
     * Only works if entertainment mode is active.
     *
     * @param frame the effect frame to send
     * @throws IllegalStateException if entertainment mode is not active
     */
    public void sendFrame(EffectFrame frame) {
        if (!entertainmentModeActive.get()) {
            throw new IllegalStateException("Entertainment mode is not active");
        }

        if (fastEffectController == null) {
            throw new IllegalStateException("Fast effect controller not initialized");
        }

        fastEffectController.sendFrame(frame);
    }

    /**
     * Get the set of light IDs currently in entertainment mode.
     *
     * @return unmodifiable set of light IDs
     */
    public Set<String> getEntertainmentLightIds() {
        return new HashSet<>(entertainmentLightIds);
    }
}
