package pw.wunderlich.lightbeat.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages audio profiles for beat detection and audio analysis.
 * Profiles are stored in /config/audio_profiles.json and can be
 * loaded and saved dynamically.
 */
public class AudioProfileManager {

    private static final Logger logger = LoggerFactory.getLogger(AudioProfileManager.class);
    private static final String DEFAULT_CONFIG_DIR = "config";
    private static final String PROFILES_FILENAME = "audio_profiles.json";

    private final File configFile;
    private final Map<String, AudioProfile> profiles;

    /**
     * Creates a new AudioProfileManager with the default config directory.
     * Initializes with default profiles if no configuration file exists.
     */
    public AudioProfileManager() {
        this(DEFAULT_CONFIG_DIR);
    }

    /**
     * Creates a new AudioProfileManager with the specified config directory.
     * Initializes with default profiles if no configuration file exists.
     *
     * @param configDir the directory where the configuration file is stored
     */
    public AudioProfileManager(String configDir) {
        this.profiles = new HashMap<>();
        
        // Create config directory if it doesn't exist
        Path configPath = Paths.get(configDir);
        try {
            Files.createDirectories(configPath);
        } catch (IOException e) {
            logger.error("Failed to create config directory: {}", configDir, e);
        }

        this.configFile = new File(configDir, PROFILES_FILENAME);
        
        // Load profiles from file or initialize with defaults
        if (configFile.exists()) {
            loadProfilesFromFile();
        } else {
            initializeDefaultProfiles();
            saveProfilesToFile();
        }
    }

    /**
     * Loads a profile by its unique identifier.
     *
     * @param id the profile identifier
     * @return the AudioProfile, or null if not found
     */
    public AudioProfile loadProfile(String id) {
        if (id == null || id.isEmpty()) {
            logger.warn("Cannot load profile with null or empty id");
            return null;
        }

        AudioProfile profile = profiles.get(id);
        if (profile == null) {
            logger.warn("Profile not found: {}", id);
        } else {
            logger.info("Loaded profile: {}", id);
        }
        return profile;
    }

    /**
     * Saves or updates a profile. If a profile with the same id exists,
     * it will be replaced.
     *
     * @param profile the profile to save
     * @return true if the profile was saved successfully, false otherwise
     */
    public boolean saveProfile(AudioProfile profile) {
        if (profile == null) {
            logger.warn("Cannot save null profile");
            return false;
        }

        if (profile.getId() == null || profile.getId().isEmpty()) {
            logger.warn("Cannot save profile with null or empty id");
            return false;
        }

        profiles.put(profile.getId(), profile);
        logger.info("Saved profile: {}", profile.getId());

        return saveProfilesToFile();
    }

    /**
     * Gets all available profile IDs.
     *
     * @return an array of profile identifiers
     */
    public String[] getAvailableProfiles() {
        return profiles.keySet().toArray(new String[0]);
    }

    /**
     * Checks if a profile with the given ID exists.
     *
     * @param id the profile identifier
     * @return true if the profile exists, false otherwise
     */
    public boolean hasProfile(String id) {
        return profiles.containsKey(id);
    }

    /**
     * Deletes a profile by its identifier.
     *
     * @param id the profile identifier
     * @return true if the profile was deleted, false if it didn't exist
     */
    public boolean deleteProfile(String id) {
        if (profiles.remove(id) != null) {
            logger.info("Deleted profile: {}", id);
            return saveProfilesToFile();
        }
        logger.warn("Profile not found for deletion: {}", id);
        return false;
    }

    /**
     * Reloads all profiles from the configuration file.
     *
     * @return true if profiles were reloaded successfully, false otherwise
     */
    public boolean reloadProfiles() {
        if (!configFile.exists()) {
            logger.warn("Config file does not exist: {}", configFile.getAbsolutePath());
            return false;
        }

        profiles.clear();
        return loadProfilesFromFile();
    }

    private void initializeDefaultProfiles() {
        logger.info("Initializing default audio profiles");

        // Techno profile: Higher sensitivity, faster beats
        AudioProfile techno = new AudioProfile("techno", "Techno");
        techno.setParameter("beatSensitivity", 6);
        techno.setParameter("minTimeBetweenBeats", 150);
        techno.setParameter("beatThresholdMultiplier", 1.4);
        techno.setParameter("description", "High-energy techno with fast, consistent beats");
        profiles.put("techno", techno);

        // House profile: Medium sensitivity, steady rhythm
        AudioProfile house = new AudioProfile("house", "House");
        house.setParameter("beatSensitivity", 5);
        house.setParameter("minTimeBetweenBeats", 200);
        house.setParameter("beatThresholdMultiplier", 1.3);
        house.setParameter("description", "Steady house rhythm with balanced sensitivity");
        profiles.put("house", house);

        // Ambient profile: Lower sensitivity, slower beats
        AudioProfile ambient = new AudioProfile("ambient", "Ambient");
        ambient.setParameter("beatSensitivity", 3);
        ambient.setParameter("minTimeBetweenBeats", 300);
        ambient.setParameter("beatThresholdMultiplier", 1.2);
        ambient.setParameter("description", "Gentle ambient music with subtle beat detection");
        profiles.put("ambient", ambient);

        logger.info("Initialized {} default profiles", profiles.size());
    }

    private boolean loadProfilesFromFile() {
        try {
            Map<String, AudioProfile> loadedProfiles = SimpleJsonUtil.readProfilesFromJson(configFile);
            profiles.putAll(loadedProfiles);
            logger.info("Loaded {} profiles from {}", profiles.size(), configFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Failed to load profiles from file: {}", configFile.getAbsolutePath(), e);
            return false;
        }
    }

    private boolean saveProfilesToFile() {
        try {
            SimpleJsonUtil.writeProfilesToJson(profiles, configFile);
            logger.info("Saved {} profiles to {}", profiles.size(), configFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Failed to save profiles to file: {}", configFile.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Gets the configuration file path.
     *
     * @return the configuration file
     */
    File getConfigFile() {
        return configFile;
    }
}
