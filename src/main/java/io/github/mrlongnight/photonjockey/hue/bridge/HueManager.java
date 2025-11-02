package io.github.mrlongnight.photonjockey.hue.bridge;

import io.github.mrlongnight.photonjockey.hue.bridge.light.Light;

import java.util.List;

/**
 * Implementing class manages the current connection state to the hue bridge
 * and passes its information to be rendered/interfaced through a {@link HueStateObserver}.
 */
public interface HueManager {

    /**
     * @return a list of all currently connected bridges
     */
    List<BridgeConnection> getBridges();

    /**
     * @param disabledLights true if disabled lights should be omitted
     * @return list containing all currently connected lights
     */
    List<Light> getLights(boolean disabledLights);

    /**
     * Sets the given parameter as the state observer to receive api callbacks.
     *
     * @param observer to receive callbacks from state changes
     */
    void setStateObserver(HueStateObserver observer);

    /**
     * @return list containing previously connected bridges
     */
    List<AccessPoint> getPreviousBridges();

    /**
     * Causes a scan for bridges on the connected network.
     */
    void doBridgesScan();

    /**
     * Tries to establish a connection with the provided access point.
     *
     * @param accessPoint wrapper containing ip of bridge and key
     */
    void setAttemptConnection(AccessPoint accessPoint);

    /**
     * Disconnects from the given bridge.
     *
     * @param bridgeConnection the bridge to disconnect from
     */
    void disconnect(BridgeConnection bridgeConnection);

    /**
     * Disconnects from all currently connected bridges.
     */
    void disconnectAll();
}
