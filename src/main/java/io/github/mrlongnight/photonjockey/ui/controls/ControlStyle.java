package io.github.mrlongnight.photonjockey.ui.controls;

/**
 * Enum representing different visual styles for value controls.
 */
public enum ControlStyle {
    /** Traditional slider control */
    SLIDER("Slider"),
    
    /** Rotary knob control */
    KNOB("Knob"),
    
    /** Value display only with small virtual display */
    DISPLAY("Display");
    
    private final String displayName;
    
    ControlStyle(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
