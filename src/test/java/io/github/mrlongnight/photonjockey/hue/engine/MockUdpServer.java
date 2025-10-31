package io.github.mrlongnight.photonjockey.hue.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Mock UDP server for testing FastEffectController.
 * Receives UDP packets and parses them to verify structure and content.
 */
public class MockUdpServer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MockUdpServer.class);
    
    private static final int BUFFER_SIZE = 2048;
    private static final int SEQUENCE_NUMBER_SIZE = 4;
    private static final int LIGHT_UPDATE_SIZE = 32;
    
    private final DatagramSocket socket;
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<ReceivedPacket> receivedPackets = new ConcurrentLinkedQueue<>();
    private Thread receiverThread;

    /**
     * Create a MockUdpServer that listens on an available port.
     *
     * @throws SocketException if the socket cannot be created
     */
    public MockUdpServer() throws SocketException {
        this.socket = new DatagramSocket(0); // Use any available port
        this.port = socket.getLocalPort();
        LOG.debug("MockUdpServer created on port {}", port);
    }

    /**
     * Create a MockUdpServer that listens on a specific port.
     *
     * @param port the port to listen on
     * @throws SocketException if the socket cannot be created
     */
    public MockUdpServer(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.port = port;
        LOG.debug("MockUdpServer created on port {}", port);
    }

    /**
     * Start the server and begin receiving packets.
     */
    public void start() {
        if (running.get()) {
            LOG.warn("Server already running");
            return;
        }
        
        running.set(true);
        receiverThread = new Thread(this::receiveLoop, "MockUdpServer-Receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
        LOG.info("MockUdpServer started on port {}", port);
    }

    /**
     * Stop the server.
     */
    public void stop() {
        if (!running.get()) {
            return;
        }
        
        running.set(false);
        
        if (receiverThread != null) {
            receiverThread.interrupt();
            try {
                receiverThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        LOG.info("MockUdpServer stopped. Received {} packets", receivedPackets.size());
    }

    @Override
    public void close() {
        stop();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Main receive loop that runs in a separate thread.
     */
    private void receiveLoop() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
                
                ReceivedPacket receivedPacket = parsePacket(data);
                receivedPackets.add(receivedPacket);
                
                LOG.debug("Received packet: sequence={}, lightCount={}", 
                    receivedPacket.sequenceNumber, receivedPacket.lightUpdates.size());
                    
            } catch (IOException e) {
                if (running.get()) {
                    LOG.error("Error receiving packet", e);
                }
            }
        }
    }

    /**
     * Parse a received UDP packet.
     *
     * @param data the packet data
     * @return parsed packet information
     */
    private ReceivedPacket parsePacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        // Read sequence number (4 bytes)
        int sequenceNumber = buffer.getInt();
        
        // Read light updates
        List<ParsedLightUpdate> lightUpdates = new ArrayList<>();
        while (buffer.remaining() >= LIGHT_UPDATE_SIZE) {
            ParsedLightUpdate update = parseLightUpdate(buffer);
            lightUpdates.add(update);
        }
        
        return new ReceivedPacket(sequenceNumber, lightUpdates, System.currentTimeMillis());
    }

    /**
     * Parse a single light update from the buffer.
     *
     * @param buffer the buffer to read from
     * @return parsed light update
     */
    private ParsedLightUpdate parseLightUpdate(ByteBuffer buffer) {
        int lightIdHash = buffer.getInt();
        int brightness = buffer.getInt();
        double hue = buffer.getDouble();
        double saturation = buffer.getDouble();
        int transitionTime = buffer.getInt();
        int reserved = buffer.getInt();
        
        return new ParsedLightUpdate(
            lightIdHash,
            brightness >= 0 ? brightness : null,
            hue >= 0 ? hue : null,
            saturation >= 0 ? saturation : null,
            transitionTime >= 0 ? transitionTime : null
        );
    }

    /**
     * Get the port the server is listening on.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Get all received packets.
     *
     * @return list of received packets
     */
    public List<ReceivedPacket> getReceivedPackets() {
        return new ArrayList<>(receivedPackets);
    }

    /**
     * Get the number of received packets.
     *
     * @return count of received packets
     */
    public int getReceivedPacketCount() {
        return receivedPackets.size();
    }

    /**
     * Clear all received packets.
     */
    public void clearReceivedPackets() {
        receivedPackets.clear();
    }

    /**
     * Wait for a specific number of packets to be received.
     *
     * @param expectedCount the number of packets to wait for
     * @param timeoutMs the timeout in milliseconds
     * @return true if the expected count was reached, false if timeout
     */
    public boolean waitForPackets(int expectedCount, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        
        while (receivedPackets.size() < expectedCount) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                return false;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return true;
    }

    /**
     * Check if the server is running.
     *
     * @return true if the server is running
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Represents a received UDP packet with parsed data.
     */
    public static class ReceivedPacket {
        private final int sequenceNumber;
        private final List<ParsedLightUpdate> lightUpdates;
        private final long timestamp;

        public ReceivedPacket(int sequenceNumber, List<ParsedLightUpdate> lightUpdates, long timestamp) {
            this.sequenceNumber = sequenceNumber;
            this.lightUpdates = lightUpdates;
            this.timestamp = timestamp;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public List<ParsedLightUpdate> getLightUpdates() {
            return lightUpdates;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Represents a parsed light update from a packet.
     */
    public static class ParsedLightUpdate {
        private final int lightIdHash;
        private final Integer brightness;
        private final Double hue;
        private final Double saturation;
        private final Integer transitionTime;

        public ParsedLightUpdate(int lightIdHash, Integer brightness, 
                                 Double hue, Double saturation, Integer transitionTime) {
            this.lightIdHash = lightIdHash;
            this.brightness = brightness;
            this.hue = hue;
            this.saturation = saturation;
            this.transitionTime = transitionTime;
        }

        public int getLightIdHash() {
            return lightIdHash;
        }

        public Integer getBrightness() {
            return brightness;
        }

        public Double getHue() {
            return hue;
        }

        public Double getSaturation() {
            return saturation;
        }

        public Integer getTransitionTime() {
            return transitionTime;
        }
    }
}
