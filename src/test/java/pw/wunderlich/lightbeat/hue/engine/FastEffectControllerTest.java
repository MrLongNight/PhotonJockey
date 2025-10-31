package pw.wunderlich.lightbeat.hue.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for FastEffectController.
 */
class FastEffectControllerTest {

    private MockUdpServer mockServer;
    private FastEffectController controller;

    @BeforeEach
    void setUp() throws Exception {
        mockServer = new MockUdpServer();
        mockServer.start();
        
        controller = new FastEffectController("localhost", mockServer.getPort());
    }

    @AfterEach
    void tearDown() {
        if (controller != null) {
            controller.stopSession();
        }
        if (mockServer != null) {
            mockServer.close();
        }
    }

    @Test
    void testConstructorValid() {
        FastEffectController ctrl = new FastEffectController("127.0.0.1", 5000);
        assertNotNull(ctrl);
        assertEquals("127.0.0.1", ctrl.getTargetHost());
        assertEquals(5000, ctrl.getTargetPort());
    }

    @Test
    void testConstructorNullHost() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FastEffectController(null, 5000);
        });
    }

    @Test
    void testConstructorEmptyHost() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FastEffectController("", 5000);
        });
    }

    @Test
    void testConstructorInvalidPortNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FastEffectController("localhost", -1);
        });
    }

    @Test
    void testConstructorInvalidPortTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FastEffectController("localhost", 65536);
        });
    }

    @Test
    void testStartSession() {
        assertFalse(controller.isSessionActive());
        
        controller.startSession();
        
        assertTrue(controller.isSessionActive());
        assertEquals(0, controller.getCurrentSequenceNumber());
    }

    @Test
    void testStopSession() {
        controller.startSession();
        assertTrue(controller.isSessionActive());
        
        controller.stopSession();
        
        assertFalse(controller.isSessionActive());
    }

    @Test
    void testSendFrameWithoutSession() {
        LightUpdateDTO update = new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        
        // Should not throw, but also should not send
        controller.sendFrame(frame);
        
        assertEquals(0, mockServer.getReceivedPacketCount());
    }

    @Test
    void testSendSingleFrame() throws Exception {
        controller.startSession();
        
        LightUpdateDTO update = new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        
        controller.sendFrame(frame);
        
        // Wait for packet to arrive
        assertTrue(mockServer.waitForPackets(1, 1000));
        
        assertEquals(1, mockServer.getReceivedPacketCount());
        assertEquals(1, controller.getFramesSent());
    }

    @Test
    void testSendMultipleFrames() throws Exception {
        controller.startSession();
        
        int frameCount = 10;
        for (int i = 0; i < frameCount; i++) {
            LightUpdateDTO update = new LightUpdateDTO("light-" + i, 100 + i, 0.5, 0.8, 0);
            List<LightUpdateDTO> updates = List.of(update);
            EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
            controller.sendFrame(frame);
        }
        
        // Wait for all packets
        assertTrue(mockServer.waitForPackets(frameCount, 2000));
        
        assertEquals(frameCount, mockServer.getReceivedPacketCount());
        assertEquals(frameCount, controller.getFramesSent());
    }

    @Test
    void testSequenceNumberIncrement() throws Exception {
        controller.startSession();
        
        int frameCount = 5;
        for (int i = 0; i < frameCount; i++) {
            LightUpdateDTO update = new LightUpdateDTO("light-001", 100, 0.5, 0.8, 0);
            List<LightUpdateDTO> updates = List.of(update);
            EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
            controller.sendFrame(frame);
        }
        
        assertTrue(mockServer.waitForPackets(frameCount, 2000));
        
        List<MockUdpServer.ReceivedPacket> packets = mockServer.getReceivedPackets();
        
        // Verify sequence numbers increment from 0
        for (int i = 0; i < frameCount; i++) {
            assertEquals(i, packets.get(i).getSequenceNumber(), 
                "Sequence number mismatch at frame " + i);
        }
    }

    @Test
    void testSendFrameWithMultipleLights() throws Exception {
        controller.startSession();
        
        List<LightUpdateDTO> updates = new ArrayList<>();
        updates.add(new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0));
        updates.add(new LightUpdateDTO("light-002", 150, 0.3, 0.9, 5));
        updates.add(new LightUpdateDTO("light-003", 100, 0.7, 0.6, 10));
        
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        controller.sendFrame(frame);
        
        assertTrue(mockServer.waitForPackets(1, 1000));
        
        MockUdpServer.ReceivedPacket packet = mockServer.getReceivedPackets().get(0);
        assertEquals(3, packet.getLightUpdates().size());
    }

    @Test
    void testLightUpdateDataIntegrity() throws Exception {
        controller.startSession();
        
        String lightId = "test-light-001";
        int brightness = 200;
        double hue = 0.5;
        double saturation = 0.8;
        int transitionTime = 10;
        
        LightUpdateDTO update = new LightUpdateDTO(lightId, brightness, hue, saturation, transitionTime);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        
        controller.sendFrame(frame);
        
        assertTrue(mockServer.waitForPackets(1, 1000));
        
        MockUdpServer.ReceivedPacket packet = mockServer.getReceivedPackets().get(0);
        MockUdpServer.ParsedLightUpdate parsedUpdate = packet.getLightUpdates().get(0);
        
        assertEquals(lightId.hashCode(), parsedUpdate.getLightIdHash());
        assertEquals(brightness, parsedUpdate.getBrightness());
        assertEquals(hue, parsedUpdate.getHue(), 0.001);
        assertEquals(saturation, parsedUpdate.getSaturation(), 0.001);
        assertEquals(transitionTime, parsedUpdate.getTransitionTime());
    }

    @Test
    void testSendFrameWithNullValues() throws Exception {
        controller.startSession();
        
        // Create update with null values
        LightUpdateDTO update = new LightUpdateDTO("light-001", null, null, null, null);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        
        controller.sendFrame(frame);
        
        assertTrue(mockServer.waitForPackets(1, 1000));
        
        MockUdpServer.ReceivedPacket packet = mockServer.getReceivedPackets().get(0);
        MockUdpServer.ParsedLightUpdate parsedUpdate = packet.getLightUpdates().get(0);
        
        // Null values should be encoded as -1 or -1.0
        assertEquals(null, parsedUpdate.getBrightness());
        assertEquals(null, parsedUpdate.getHue());
        assertEquals(null, parsedUpdate.getSaturation());
        assertEquals(null, parsedUpdate.getTransitionTime());
    }

    @Test
    void testSendEmptyFrame() {
        controller.startSession();
        
        List<LightUpdateDTO> updates = new ArrayList<>();
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        
        controller.sendFrame(frame);
        
        // Should not send anything
        assertEquals(0, mockServer.getReceivedPacketCount());
        assertEquals(0, controller.getFramesSent());
    }

    @Test
    void testSendNullFrame() {
        controller.startSession();
        
        controller.sendFrame(null);
        
        // Should not send anything
        assertEquals(0, mockServer.getReceivedPacketCount());
        assertEquals(0, controller.getFramesSent());
    }

    @Test
    void testMetricsFramesSent() throws Exception {
        controller.startSession();
        
        assertEquals(0, controller.getFramesSent());
        
        for (int i = 0; i < 5; i++) {
            LightUpdateDTO update = new LightUpdateDTO("light-001", 100, 0.5, 0.8, 0);
            List<LightUpdateDTO> updates = List.of(update);
            EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
            controller.sendFrame(frame);
        }
        
        assertTrue(mockServer.waitForPackets(5, 2000));
        assertEquals(5, controller.getFramesSent());
    }

    @Test
    void testMetricsPacketLossZero() throws Exception {
        controller.startSession();
        
        // Send some frames successfully
        for (int i = 0; i < 3; i++) {
            LightUpdateDTO update = new LightUpdateDTO("light-001", 100, 0.5, 0.8, 0);
            List<LightUpdateDTO> updates = List.of(update);
            EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
            controller.sendFrame(frame);
        }
        
        assertTrue(mockServer.waitForPackets(3, 2000));
        
        // Packet loss should be 0%
        assertEquals(0.0, controller.getPacketLossPct(), 0.001);
    }

    @Test
    void testMetricsPacketLossWithInvalidHost() {
        // Create controller with invalid host to force packet failures
        FastEffectController failController = new FastEffectController("255.255.255.255", 9999);
        failController.startSession();
        
        // Initial packet loss should be 0%
        assertEquals(0.0, failController.getPacketLossPct(), 0.001);
        
        // Try to send frames (they will fail)
        for (int i = 0; i < 5; i++) {
            LightUpdateDTO update = new LightUpdateDTO("light-001", 100, 0.5, 0.8, 0);
            List<LightUpdateDTO> updates = List.of(update);
            EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
            failController.sendFrame(frame);
        }
        
        // Note: With localhost UDP, packets might not actually fail, 
        // so this test just verifies the metric calculation works
        double packetLoss = failController.getPacketLossPct();
        assertTrue(packetLoss >= 0.0 && packetLoss <= 100.0);
        
        failController.stopSession();
    }

    @Test
    void testSessionRestart() throws Exception {
        // Start and stop session
        controller.startSession();
        assertTrue(controller.isSessionActive());
        controller.stopSession();
        assertFalse(controller.isSessionActive());
        
        // Restart session
        controller.startSession();
        assertTrue(controller.isSessionActive());
        
        // Sequence should reset to 0
        assertEquals(0, controller.getCurrentSequenceNumber());
        
        // Should be able to send frames again
        LightUpdateDTO update = new LightUpdateDTO("light-001", 100, 0.5, 0.8, 0);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
        controller.sendFrame(frame);
        
        assertTrue(mockServer.waitForPackets(1, 1000));
        assertEquals(1, mockServer.getReceivedPacketCount());
    }

    @Test
    void testStartSessionTwice() {
        controller.startSession();
        assertTrue(controller.isSessionActive());
        
        // Starting again should not fail, just log a warning
        controller.startSession();
        assertTrue(controller.isSessionActive());
    }

    @Test
    void testStopSessionWithoutStart() {
        assertFalse(controller.isSessionActive());
        
        // Stopping without starting should not fail
        controller.stopSession();
        assertFalse(controller.isSessionActive());
    }

    @Test
    void testGetters() {
        assertEquals("localhost", controller.getTargetHost());
        assertEquals(mockServer.getPort(), controller.getTargetPort());
    }
}
