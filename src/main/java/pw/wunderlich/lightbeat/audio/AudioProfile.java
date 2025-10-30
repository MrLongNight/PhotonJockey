package pw.wunderlich.lightbeat.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an audio profile with customizable parameters for beat detection
 * and audio analysis. Each profile has a unique identifier and a set of
 * parameters stored as key-value pairs.
 */
public class AudioProfile {

    private final String id;
    private final String name;
    private final Map<String, Object> parameters;

    /**
     * Creates a new AudioProfile with the specified id and name.
     *
     * @param id   the unique identifier for this profile
     * @param name the display name for this profile
     */
    public AudioProfile(String id, String name) {
        this.id = Objects.requireNonNull(id, "Profile id cannot be null");
        this.name = Objects.requireNonNull(name, "Profile name cannot be null");
        this.parameters = new HashMap<>();
    }

    /**
     * Creates a new AudioProfile with the specified id, name, and parameters.
     *
     * @param id         the unique identifier for this profile
     * @param name       the display name for this profile
     * @param parameters the parameters for this profile
     */
    public AudioProfile(String id, String name, Map<String, Object> parameters) {
        this.id = Objects.requireNonNull(id, "Profile id cannot be null");
        this.name = Objects.requireNonNull(name, "Profile name cannot be null");
        this.parameters = new HashMap<>(parameters);
    }

    /**
     * Gets the unique identifier for this profile.
     *
     * @return the profile id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the display name for this profile.
     *
     * @return the profile name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all parameters for this profile.
     *
     * @return a copy of the parameters map
     */
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    /**
     * Sets a parameter value for this profile.
     *
     * @param key   the parameter key
     * @param value the parameter value
     */
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    /**
     * Gets a parameter value for this profile.
     *
     * @param key the parameter key
     * @return the parameter value, or null if not found
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * Gets an integer parameter value for this profile.
     *
     * @param key          the parameter key
     * @param defaultValue the default value if not found
     * @return the parameter value as an integer
     */
    public int getIntParameter(String key, int defaultValue) {
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    /**
     * Gets a double parameter value for this profile.
     *
     * @param key          the parameter key
     * @param defaultValue the default value if not found
     * @return the parameter value as a double
     */
    public double getDoubleParameter(String key, double defaultValue) {
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioProfile that = (AudioProfile) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parameters);
    }

    @Override
    public String toString() {
        return "AudioProfile{id='" + id + "', name='" + name + "', parameters=" + parameters + '}';
    }
}
