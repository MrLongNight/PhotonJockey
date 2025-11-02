package io.github.mrlongnight.photonjockey.hue.bridge;

import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PJHueManagerTest {

    @Mock
    private Config config;
    @Mock
    private AppTaskOrchestrator taskOrchestrator;
    @Mock
    private HueStateObserver stateObserver;
    @Mock
    private BridgeConnection bridgeConnection1;
    @Mock
    private BridgeConnection bridgeConnection2;
    @Mock
    private Light light1;
    @Mock
    private Light light2;
    @Mock
    private State light1State;
    @Mock
    private State light2State;

    @Captor
    private ArgumentCaptor<BridgeConnection.ConnectionListener> listenerCaptor;

    private PJHueManager hueManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hueManager = spy(new PJHueManager(config, taskOrchestrator));
        hueManager.setStateObserver(stateObserver);
    }

    private void simulateSuccessfulConnection(String ip, BridgeConnection connectionToReturn) {
        AccessPoint ap = new AccessPoint(ip, "key");

        doAnswer(invocation -> {
            BridgeConnection.ConnectionListener listener = invocation.getArgument(2);
            listener.connectionSuccess(connectionToReturn, ap.key(), "Test Bridge", "hash");
            return connectionToReturn;
        }).when(hueManager).createBridgeConnection(any(), any(), any());

        hueManager.setAttemptConnection(ap);
    }

    @Test
    void testConnectMultipleBridges() {
        simulateSuccessfulConnection("ip1", bridgeConnection1);
        simulateSuccessfulConnection("ip2", bridgeConnection2);

        assertEquals(2, hueManager.getBridges().size());
        assertTrue(hueManager.getBridges().contains(bridgeConnection1));
        assertTrue(hueManager.getBridges().contains(bridgeConnection2));
    }

    @Test
    void testGetLightsFromMultipleBridges() {
        when(bridgeConnection1.getKnownLights()).thenReturn(List.of(light1));
        when(bridgeConnection2.getKnownLights()).thenReturn(List.of(light2));
        when(light1.getId()).thenReturn("1");
        when(light2.getId()).thenReturn("2");
        when(config.getStringList(any())).thenReturn(Collections.emptyList());
        when(light1.getState()).thenReturn(light1State);
        when(light2.getState()).thenReturn(light2State);

        simulateSuccessfulConnection("ip1", bridgeConnection1);
        simulateSuccessfulConnection("ip2", bridgeConnection2);

        assertEquals(2, hueManager.getLights(false).size());
    }

    @Test
    void testDisconnectSingleBridge() {
        when(bridgeConnection1.getAccessPoint()).thenReturn(new AccessPoint("ip1", null));
        when(bridgeConnection2.getAccessPoint()).thenReturn(new AccessPoint("ip2", null));
        simulateSuccessfulConnection("ip1", bridgeConnection1);
        simulateSuccessfulConnection("ip2", bridgeConnection2);

        hueManager.disconnect(bridgeConnection1);

        assertEquals(1, hueManager.getBridges().size());
        assertTrue(hueManager.getBridges().contains(bridgeConnection2));
        verify(bridgeConnection1, times(1)).disconnect();
    }

    @Test
    void testDisconnectAllBridges() {
        simulateSuccessfulConnection("ip1", bridgeConnection1);
        simulateSuccessfulConnection("ip2", bridgeConnection2);

        hueManager.disconnectAll();

        assertEquals(0, hueManager.getBridges().size());
        verify(bridgeConnection1, times(1)).disconnect();
        verify(bridgeConnection2, times(1)).disconnect();
    }
}
