package io.github.mrlongnight.photonjockey.hue.visualizer.effect;

import io.github.mrlongnight.photonjockey.hue.bridge.light.Light;
import io.github.mrlongnight.photonjockey.hue.visualizer.LightUpdate;

import java.util.Random;

/**
 * Abstract implementation of {@link LightEffect}.
 * Implementations must override {@link #execute(LightUpdate)} and can access the current
 * {@link LightUpdate} via field.
 */
abstract class AbstractEffect implements LightEffect {

    final Random rnd = new Random();


    @Override
    public void beatReceived(LightUpdate lightUpdate) {
        execute(lightUpdate);
    }

    abstract void execute(LightUpdate lightUpdate);

    void unsetControllingEffect(LightUpdate lightUpdate) {
        for (Light light : lightUpdate.getLights()) {
            light.getColorController().unsetControllingEffect(this);
            light.getStrobeController().unsetControllingEffect(this);
            light.getBrightnessController().unsetControllingEffect(this);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
