package io.github.mrlongnight.photonjockey.hue.bridge.light;

import io.github.zeroone3010.yahueapi.State;
import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.hue.bridge.light.controller.BrightnessController;
import io.github.mrlongnight.photonjockey.hue.bridge.light.controller.ColorController;
import io.github.mrlongnight.photonjockey.hue.bridge.light.controller.StrobeController;


/**
 * Default and thread safe {@link Light} implementation.
 */
public class PJLight implements Light {

    private final io.github.zeroone3010.yahueapi.Light light;
    private final UpdateQueue updateQueue;

    private final ColorController colorController;
    private final BrightnessController brightnessController;
    private final StrobeController strobeController;

    private volatile LightStateBuilder currentBuilder;
    private volatile LightStateBuilder builderToCopyAfterTurningOn;
    private volatile boolean isOn;
    private volatile boolean forceOnStateNextUpdate = false;

    private volatile State storedState = null;


    public PJLight(io.github.zeroone3010.yahueapi.Light apiLight, AppTaskOrchestrator taskOrchestrator) {
        this.light = apiLight;
        this.updateQueue = new UpdateQueue(apiLight, taskOrchestrator);

        this.colorController = new ColorController(this);
        this.brightnessController = new BrightnessController(this);
        this.strobeController = new StrobeController(this, taskOrchestrator);

        this.currentBuilder = LightStateBuilder.create();
        this.builderToCopyAfterTurningOn = LightStateBuilder.create();

        this.isOn = apiLight.getState().getOn();
    }

    @Override
    public io.github.zeroone3010.yahueapi.Light getBase() {
        return light;
    }

    @Override
    public ColorController getColorController() {
        return colorController;
    }

    @Override
    public BrightnessController getBrightnessController() {
        return brightnessController;
    }

    @Override
    public StrobeController getStrobeController() {
        return strobeController;
    }

    @Override
    public LightStateBuilder getStateBuilder() {
        return isOn ? currentBuilder : builderToCopyAfterTurningOn;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    @Override
    public synchronized void setOn(boolean on) {

        if (strobeController.isStrobing()) {
            strobeController.cancelStrobe();
        }

        if (this.isOn == on) {
            return;
        }

        if (/* turned */ on) {
            currentBuilder.copyFromBuilder(builderToCopyAfterTurningOn);
            brightnessController.forceBrightnessUpdate();
            this.forceOnStateNextUpdate = true;
        } else {
            builderToCopyAfterTurningOn = LightStateBuilder.create();
        }

        currentBuilder.setOn(on);
        this.isOn = on;
    }

    @Override
    public synchronized void doLightUpdate(int transitionTime) {

        strobeController.applyUpdates();
        colorController.applyUpdates();

        if (!currentBuilder.isDefault() || brightnessController.isBrightnessWasIncreased()) {
            brightnessController.applyUpdates();
            updateQueue.addUpdate(currentBuilder.getLightState(), forceOnStateNextUpdate);
            forceOnStateNextUpdate = false;
        }

        if (transitionTime > 0) {
            currentBuilder = LightStateBuilder.create().setTransitionTime(transitionTime);

            colorController.applyFadeUpdates();
            brightnessController.applyFadeUpdates();

            if (!currentBuilder.isDefault()) {
                updateQueue.addUpdate(currentBuilder.getLightState(), false);
            }
        }

        this.currentBuilder = LightStateBuilder.create();
    }

    @Override
    public void storeState() {
        this.storedState = light.getState();
        this.storedState.removeAlert();
    }

    @Override
    public void restoreState() {
        if (storedState != null) {
            updateQueue.addUpdate(storedState, true);
            storedState = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof PJLight && this.light.getId().equals(((PJLight) o).light.getId());
    }

    @Override
    public int hashCode() {
        return light.getId().hashCode();
    }
}
