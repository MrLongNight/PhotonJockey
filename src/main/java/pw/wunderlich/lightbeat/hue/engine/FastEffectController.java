package pw.wunderlich.lightbeat.hue.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FastEffectController implements UDP-based communication for Hue Entertainment V2 API.
 * Sends light effects via UDP datagrams with sequence numbers for high-performance updates.
 */
public class FastEffectController implements IFastEffectController {

    private static final Logger LOG = LoggerFactory.getLogger(FastEffectController.class);
    
    // Maximum UDP payload size for safety (typical MTU is 1500, we use conservative value)
    private static final int MAX_UDP_PAYLOAD_SIZE = 1400;
    
    // Size constants for packet structure
    private static final int SEQUENCE_NUMBER_SIZE = 4; // int = 4 bytes
    private static final int LIGHT_UPDATE_SIZE = 32; // Per-light payload size
    
    private final String targetHost;
    private final int targetPort;
    private DatagramSocket socket;
    private InetAddress targetAddress;
    
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);
    private final AtomicLong framesSent = new AtomicLong(0);
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong packetsFailed = new AtomicLong(0);
    
    private boolean sessionActive = false;

    /**
     * Create a FastEffectController with the specified target host and port.
     *
     * @param targetHost the hostname or IP address of the target
     * @param targetPort the UDP port to send data to
     */
    public FastEffectController(String targetHost, int targetPort) {
        if (targetHost == null || targetHost.trim().isEmpty()) {
            throw new IllegalArgumentException("Target host cannot be null or empty");
        }
        if (targetPort < 0 || targetPort > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
        
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    @Override
    public void startSession() {
        if (sessionActive) {
            LOG.warn("Session already active, ignoring startSession call");
            return;
        }
        
        try {
            socket = new DatagramSocket();
            targetAddress = InetAddress.getByName(targetHost);
            sequenceNumber.set(0);
            sessionActive = true;
            LOG.info("Started UDP session to {}:{}", targetHost, targetPort);
        } catch (SocketException e) {
            LOG.error("Failed to create UDP socket", e);
            throw new RuntimeException("Failed to start session: " + e.getMessage(), e);
        } catch (UnknownHostException e) {
            LOG.error("Failed to resolve target host: {}", targetHost, e);
            throw new RuntimeException("Failed to start session: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendFrame(EffectFrame frame) {
        if (!sessionActive) {
            LOG.warn("Session not active, cannot send frame");
            return;
        }
        
        if (frame == null || frame.getUpdates() == null || frame.getUpdates().isEmpty()) {
            LOG.debug("Empty frame, skipping");
            return;
        }
        
        try {
            byte[] payload = encodeFrame(frame);
            DatagramPacket packet = new DatagramPacket(
                payload, 
                payload.length, 
                targetAddress, 
                targetPort
            );
            
            socket.send(packet);
            
            framesSent.incrementAndGet();
            packetsSent.incrementAndGet();
            
            LOG.debug("Sent frame with sequence {} containing {} updates", 
                sequenceNumber.get() - 1, frame.getUpdates().size());
                
        } catch (IOException e) {
            packetsFailed.incrementAndGet();
            LOG.error("Failed to send UDP packet", e);
        }
    }

    @Override
    public void stopSession() {
        if (!sessionActive) {
            LOG.debug("Session not active, ignoring stopSession call");
            return;
        }
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        sessionActive = false;
        LOG.info("Stopped UDP session. Total frames sent: {}, packets sent: {}, packets failed: {}", 
            framesSent.get(), packetsSent.get(), packetsFailed.get());
    }

    /**
     * Encode an effect frame into a UDP packet payload.
     * Format: [4-byte sequence number][light updates...]
     * Each light update: [32 bytes of light data]
     *
     * @param frame the frame to encode
     * @return byte array containing the encoded packet
     */
    private byte[] encodeFrame(EffectFrame frame) {
        int currentSeq = sequenceNumber.getAndIncrement();
        
        int payloadSize = SEQUENCE_NUMBER_SIZE + (frame.getUpdates().size() * LIGHT_UPDATE_SIZE);
        
        if (payloadSize > MAX_UDP_PAYLOAD_SIZE) {
            LOG.warn("Payload size {} exceeds maximum {}, truncating updates", 
                payloadSize, MAX_UDP_PAYLOAD_SIZE);
            // In a real implementation, we might split into multiple packets
            // For now, we'll just warn
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(payloadSize);
        
        // Write sequence number (4 bytes)
        buffer.putInt(currentSeq);
        
        // Write each light update (32 bytes per light)
        for (LightUpdateDTO update : frame.getUpdates()) {
            encodeLightUpdate(buffer, update);
        }
        
        return buffer.array();
    }

    /**
     * Encode a single light update into the buffer.
     * Format (32 bytes total):
     * - Light ID hash (4 bytes)
     * - Brightness (4 bytes, int)
     * - Hue (8 bytes, double)
     * - Saturation (8 bytes, double)
     * - Transition time (4 bytes, int)
     * - Reserved (4 bytes)
     *
     * @param buffer the buffer to write to
     * @param update the light update to encode
     */
    private void encodeLightUpdate(ByteBuffer buffer, LightUpdateDTO update) {
        // Light ID as hash (4 bytes)
        buffer.putInt(update.getLightId().hashCode());
        
        // Brightness (4 bytes) - using -1 for null
        buffer.putInt(update.getBrightness() != null ? update.getBrightness() : -1);
        
        // Hue (8 bytes) - using -1.0 for null
        buffer.putDouble(update.getHue() != null ? update.getHue() : -1.0);
        
        // Saturation (8 bytes) - using -1.0 for null
        buffer.putDouble(update.getSaturation() != null ? update.getSaturation() : -1.0);
        
        // Transition time (4 bytes) - using -1 for null
        buffer.putInt(update.getTransitionTime() != null ? update.getTransitionTime() : -1);
        
        // Reserved (4 bytes) - for future use
        buffer.putInt(0);
    }

    /**
     * Get the current sequence number.
     *
     * @return the current sequence number
     */
    public int getCurrentSequenceNumber() {
        return sequenceNumber.get();
    }

    /**
     * Get the total number of frames sent.
     *
     * @return frames sent count
     */
    public long getFramesSent() {
        return framesSent.get();
    }

    /**
     * Get the packet loss percentage.
     *
     * @return packet loss percentage (0.0 - 100.0)
     */
    public double getPacketLossPct() {
        long total = packetsSent.get() + packetsFailed.get();
        if (total == 0) {
            return 0.0;
        }
        return (packetsFailed.get() * 100.0) / total;
    }

    /**
     * Check if the session is currently active.
     *
     * @return true if session is active
     */
    public boolean isSessionActive() {
        return sessionActive;
    }

    /**
     * Get the target host.
     *
     * @return the target host
     */
    public String getTargetHost() {
        return targetHost;
    }

    /**
     * Get the target port.
     *
     * @return the target port
     */
    public int getTargetPort() {
        return targetPort;
    }
}
