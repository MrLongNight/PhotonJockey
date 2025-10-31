package io.github.mrlongnight.photonjockey.hue.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for MockUdpServer.
 */
class MockUdpServerTest {

    private MockUdpServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockUdpServer();
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.close();
        }
    }

    @Test
    void testServerCreation() {
        assertNotNull(server);
        assertTrue(server.getPort() > 0);
        assertTrue(server.isRunning());
    }

    @Test
    void testServerStartStop() {
        assertTrue(server.isRunning());
        
        server.stop();
        assertFalse(server.isRunning());
    }

    @Test
    void testReceiveSinglePacket() throws Exception {
        // Send a test packet
        sendTestPacket(server.getPort(), 1, 1);
        
        // Wait for packet to be received
        assertTrue(server.waitForPackets(1, 1000));
        
        List<MockUdpServer.ReceivedPacket> packets = server.getReceivedPackets();
        assertEquals(1, packets.size());
        
        MockUdpServer.ReceivedPacket packet = packets.get(0);
        assertEquals(1, packet.getSequenceNumber());
        assertEquals(1, packet.getLightUpdates().size());
    }

    @Test
    void testReceiveMultiplePackets() throws Exception {
        int packetCount = 5;
        
        // Send multiple packets
        for (int i = 0; i < packetCount; i++) {
            sendTestPacket(server.getPort(), i, 1);
            Thread.sleep(10); // Small delay between packets
        }
        
        // Wait for all packets
        assertTrue(server.waitForPackets(packetCount, 2000));
        
        List<MockUdpServer.ReceivedPacket> packets = server.getReceivedPackets();
        assertEquals(packetCount, packets.size());
        
        // Verify sequence numbers
        for (int i = 0; i < packetCount; i++) {
            assertEquals(i, packets.get(i).getSequenceNumber());
        }
    }

    @Test
    void testSequenceNumberIncrement() throws Exception {
        // Send packets with incrementing sequence numbers
        for (int i = 0; i < 10; i++) {
            sendTestPacket(server.getPort(), i, 1);
        }
        
        assertTrue(server.waitForPackets(10, 2000));
        
        List<MockUdpServer.ReceivedPacket> packets = server.getReceivedPackets();
        
        // Verify sequence numbers increment correctly
        for (int i = 0; i < 10; i++) {
            assertEquals(i, packets.get(i).getSequenceNumber(), 
                "Sequence number mismatch at index " + i);
        }
    }

    @Test
    void testParseMultipleLightUpdates() throws Exception {
        // Send a packet with multiple light updates
        sendTestPacket(server.getPort(), 100, 3);
        
        assertTrue(server.waitForPackets(1, 1000));
        
        MockUdpServer.ReceivedPacket packet = server.getReceivedPackets().get(0);
        assertEquals(100, packet.getSequenceNumber());
        assertEquals(3, packet.getLightUpdates().size());
    }

    @Test
    void testParseLightUpdateData() throws Exception {
        // Send a packet with specific light data
        int sequenceNum = 42;
        int lightIdHash = "test-light-001".hashCode();
        int brightness = 200;
        double hue = 0.5;
        double saturation = 0.8;
        int transitionTime = 10;
        
        sendDetailedTestPacket(server.getPort(), sequenceNum, 
            lightIdHash, brightness, hue, saturation, transitionTime);
        
        assertTrue(server.waitForPackets(1, 1000));
        
        MockUdpServer.ReceivedPacket packet = server.getReceivedPackets().get(0);
        assertEquals(sequenceNum, packet.getSequenceNumber());
        
        List<MockUdpServer.ParsedLightUpdate> updates = packet.getLightUpdates();
        assertEquals(1, updates.size());
        
        MockUdpServer.ParsedLightUpdate update = updates.get(0);
        assertEquals(lightIdHash, update.getLightIdHash());
        assertEquals(brightness, update.getBrightness());
        assertEquals(hue, update.getHue(), 0.001);
        assertEquals(saturation, update.getSaturation(), 0.001);
        assertEquals(transitionTime, update.getTransitionTime());
    }

    @Test
    void testClearReceivedPackets() throws Exception {
        // Send some packets
        sendTestPacket(server.getPort(), 1, 1);
        sendTestPacket(server.getPort(), 2, 1);
        
        assertTrue(server.waitForPackets(2, 1000));
        assertEquals(2, server.getReceivedPacketCount());
        
        // Clear packets
        server.clearReceivedPackets();
        assertEquals(0, server.getReceivedPacketCount());
    }

    @Test
    void testWaitForPacketsTimeout() {
        // Wait for packets that will never arrive
        assertFalse(server.waitForPackets(10, 100));
    }

    @Test
    void testGetPort() {
        int port = server.getPort();
        assertTrue(port > 0 && port <= 65535);
    }

    /**
     * Helper method to send a test packet with a sequence number and number of light updates.
     */
    private void sendTestPacket(int port, int sequenceNum, int lightCount) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + (32 * lightCount));
        
        // Write sequence number
        buffer.putInt(sequenceNum);
        
        // Write light updates
        for (int i = 0; i < lightCount; i++) {
            // Light ID hash
            buffer.putInt(("light-" + i).hashCode());
            // Brightness
            buffer.putInt(100 + i);
            // Hue
            buffer.putDouble(0.5);
            // Saturation
            buffer.putDouble(0.8);
            // Transition time
            buffer.putInt(0);
            // Reserved
            buffer.putInt(0);
        }
        
        byte[] data = buffer.array();
        
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(
                data, 
                data.length, 
                InetAddress.getLoopbackAddress(), 
                port
            );
            socket.send(packet);
        }
    }

    /**
     * Helper method to send a detailed test packet with specific light update data.
     */
    private void sendDetailedTestPacket(int port, int sequenceNum, 
                                       int lightIdHash, int brightness, 
                                       double hue, double saturation, 
                                       int transitionTime) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + 32);
        
        // Write sequence number
        buffer.putInt(sequenceNum);
        
        // Write light update
        buffer.putInt(lightIdHash);
        buffer.putInt(brightness);
        buffer.putDouble(hue);
        buffer.putDouble(saturation);
        buffer.putInt(transitionTime);
        buffer.putInt(0); // Reserved
        
        byte[] data = buffer.array();
        
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(
                data, 
                data.length, 
                InetAddress.getLoopbackAddress(), 
                port
            );
            socket.send(packet);
        }
    }
}
