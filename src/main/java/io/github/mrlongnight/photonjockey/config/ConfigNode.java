package io.github.mrlongnight.photonjockey.config;

/**
 * Contains list of all config nodes used.
 */
public enum ConfigNode {

    AUTOSTART("autostart"),
    BEAT_MIN_TIME_BETWEEN("beat.mintimebetween"),
    BEAT_SENSITIVITY("beat.sensitivity"),
    BEAT_BASS_ONLY_MODE("beat.bassonlymode"),
    LOW_FREQ("audio.low_freq"),
    MID_FREQ("audio.mid_freq"),
    HIGH_FREQ("audio.high_freq"),
    BRIDGE_USERNAME_LEGACY("bridge.username"),
    BRIDGE_IPADDRESS_LEGACY("bridge.ipaddress"),
    BRIDGE_LIST("bridge.list"),
    BRIGHTNESS_FADE_DIFFERENCE("brightness.fade.difference"),
    BRIGHTNESS_FADE_MAX_TIME("brightness.fade.maxtime"),
    BRIGHTNESS_MIN("brightness.min"),
    BRIGHTNESS_MAX("brightness.max"),
    CUSTOM(null),
    COLOR_RANDOMIZATION_RANGE("color.randomization"),
    COLOR_SET_LIST("color.set.list"),
    COLOR_SET_PRESET_LIST("color.set.preset.list"),
    COLOR_SET_SELECTED("color.set.selected"),
    EFFECT_ALERT("effect.alert"),
    EFFECT_COLOR_STROBE("effect.colorstrobe"),
    EFFECT_STROBE("effect.strobe"),
    LAST_AUDIO_SOURCE("frame.lastaudiosource"),
    LIGHT_AMOUNT_PROBABILITY("lights.amountprobability"),
    LIGHTS_DISABLED("lights.disabled"),
    LOG_PATH("log.path"),
    CONSOLE_LOG_LEVEL("log.level.console"),
    FILE_LOG_LEVEL("log.level.file"),
    SHOW_ADVANCED_SETTINGS("frame.showadvanced"),
    UPDATE_DISABLE_NOTIFICATION("frame.updatedisablenotification"),
    WINDOW_LOCATION("window.location"),
    THEME("window.theme"),

    // Added Nodes
    BEAT_DELAY("beat.delay"),
    LIGHTS_PER_BEAT("lights.perbeat"),
    HUE_MAX_FADE_TIME("hue.maxfadetime"),
    LIGHT_THEME_ENABLED("window.lighttheme.enabled"),
    HUE_ENTERTAINMENT_GROUP("hue.entertainmentgroup"),
    CUSTOM_COLOR_SET_PREFIX("color.set.custom.");


    private String key;

    ConfigNode(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    private void setKey(String key) {
        if (CUSTOM.equals(this)) {
            this.key = key;
        }
    }

    public static ConfigNode getCustomNode(String key) {
        ConfigNode node = CUSTOM;
        node.setKey(key.replace(" ", "_"));
        return node;
    }
}
