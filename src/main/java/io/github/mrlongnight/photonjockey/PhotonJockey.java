package io.github.mrlongnight.photonjockey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.config.PJConfig;
import io.github.mrlongnight.photonjockey.gui.FrameManager;
import io.github.mrlongnight.photonjockey.hue.bridge.AccessPoint;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;

import java.util.Objects;

/**
 * Entry point for application. Starts modules to bootstrap the application.
 *
 * @author Fabian Prieto Wunderlich
 */
public class PhotonJockey {

    private static final Logger logger = LoggerFactory.getLogger(PhotonJockey.class);

    public static void main(String[] args) {
        new PhotonJockey();
    }

    public static String getVersion() {
        String version = PhotonJockey.class.getPackage().getImplementationVersion();
        return Objects.requireNonNullElse(version, "");
    }


    private PhotonJockey() {

        logger.info("PhotonJockey v{} starting", getVersion());

        final var taskOrchestrator = new AppTaskOrchestrator();
        final var config = new PJConfig();

        final var audioReader = new PJAudioReader(config, taskOrchestrator);
        final var hueManager = new PJHueManager(config, taskOrchestrator);

        // enter swing UI
        new FrameManager(config, taskOrchestrator, audioReader, audioReader, hueManager);

        final var accessPoints = hueManager.getPreviousBridges();
        if (accessPoints.isEmpty()) {
            var accessPoint = getLastConnectedLegacy(config);
            if (accessPoint != null) {
                hueManager.setAttemptConnection(accessPoint);
            } else {
                hueManager.doBridgesScan();
            }
        } else {
            hueManager.setAttemptConnection(accessPoints.getFirst());
        }
    }

    private AccessPoint getLastConnectedLegacy(Config config) {
        // will be removed sooner or later, alongside their config nodes
        String oldIp = config.get(ConfigNode.BRIDGE_IPADDRESS_LEGACY);
        if (oldIp != null) {
            String oldUsername = config.get(ConfigNode.BRIDGE_USERNAME_LEGACY);
            return new AccessPoint(oldIp, oldUsername);
        }
        return null;
    }
}
