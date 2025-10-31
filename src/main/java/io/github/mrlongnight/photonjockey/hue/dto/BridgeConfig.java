package io.github.mrlongnight.photonjockey.hue.dto;

import java.util.Objects;

/**
 * Represents a Philips Hue bridge configuration.
 */
public class BridgeConfig {
    private String id;
    private String ip;
    private String username;

    /**
     * Default constructor for JSON deserialization.
     */
    public BridgeConfig() {
    }

    /**
     * Creates a new bridge configuration.
     *
     * @param id Unique identifier for the bridge
     * @param ip IP address of the bridge
     * @param username API username for the bridge (optional)
     */
    public BridgeConfig(String id, String ip, String username) {
        this.id = id;
        this.ip = ip;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BridgeConfig that = (BridgeConfig) o;
        return Objects.equals(id, that.id)
                && Objects.equals(ip, that.ip)
                && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, username);
    }

    @Override
    public String toString() {
        return "BridgeConfig{id='" + id + "', ip='" + ip + "', username='" + username + "'}";
    }
}
