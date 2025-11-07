package io.github.mrlongnight.photonjockey.ui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A configurable value control that can display as a slider, knob, or display.
 */
public class ConfigurableValueControl extends VBox {
    
    private final ObjectProperty<ControlStyle> controlStyle = new SimpleObjectProperty<>(ControlStyle.SLIDER);
    
    private Slider slider;
    private RotaryKnob knob;
    private ValueDisplay display;
    
    private double min = 0;
    private double max = 100;
    private double value = 0;
    private String labelText = "";
    
    public ConfigurableValueControl() {
        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);
        
        controlStyle.addListener((obs, oldStyle, newStyle) -> rebuildControl());
        
        rebuildControl();
    }
    
    public ConfigurableValueControl(String labelText, double min, double max, double value) {
        this();
        this.labelText = labelText;
        this.min = min;
        this.max = max;
        this.value = value;
        rebuildControl();
    }
    
    private void rebuildControl() {
        getChildren().clear();
        
        switch (controlStyle.get()) {
            case SLIDER:
                buildSliderControl();
                break;
            case KNOB:
                buildKnobControl();
                break;
            case DISPLAY:
                buildDisplayControl();
                break;
        }
    }
    
    private void buildSliderControl() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.setMinWidth(120);
        
        slider = new Slider(min, max, value);
        slider.setPrefWidth(300);
        slider.setShowTickMarks(true);
        
        Label valueLabel = new Label();
        valueLabel.setMinWidth(50);
        valueLabel.textProperty().bind(slider.valueProperty().asString("%.0f"));
        
        container.getChildren().addAll(label, slider, valueLabel);
        getChildren().add(container);
    }
    
    private void buildKnobControl() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.setMinWidth(120);
        
        knob = new RotaryKnob();
        knob.setMin(min);
        knob.setMax(max);
        knob.setValue(value);
        
        container.getChildren().addAll(label, knob);
        getChildren().add(container);
    }
    
    private void buildDisplayControl() {
        display = new ValueDisplay();
        display.setDisplayName(labelText);
        display.setMin(min);
        display.setMax(max);
        display.setValue(value);
        
        getChildren().add(display);
    }
    
    // Getters for the current active control
    public DoubleProperty valueProperty() {
        switch (controlStyle.get()) {
            case SLIDER:
                return slider != null ? slider.valueProperty() : null;
            case KNOB:
                return knob != null ? knob.valueProperty() : null;
            case DISPLAY:
                return display != null ? display.valueProperty() : null;
            default:
                return null;
        }
    }
    
    public double getValue() {
        DoubleProperty prop = valueProperty();
        return prop != null ? prop.get() : value;
    }
    
    public void setValue(double value) {
        this.value = value;
        DoubleProperty prop = valueProperty();
        if (prop != null) {
            prop.set(value);
        }
    }
    
    public void setMin(double min) {
        this.min = min;
        if (slider != null) {
            slider.setMin(min);
        }
        if (knob != null) {
            knob.setMin(min);
        }
        if (display != null) {
            display.setMin(min);
        }
    }
    
    public void setMax(double max) {
        this.max = max;
        if (slider != null) {
            slider.setMax(max);
        }
        if (knob != null) {
            knob.setMax(max);
        }
        if (display != null) {
            display.setMax(max);
        }
    }
    
    public void setLabelText(String labelText) {
        this.labelText = labelText;
        rebuildControl();
    }
    
    public ControlStyle getControlStyle() {
        return controlStyle.get();
    }
    
    public void setControlStyle(ControlStyle style) {
        this.controlStyle.set(style);
    }
    
    public ObjectProperty<ControlStyle> controlStyleProperty() {
        return controlStyle;
    }
}
