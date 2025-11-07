package io.github.mrlongnight.photonjockey.ui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A display-only control that shows a value with a virtual display appearance.
 */
public class ValueDisplay extends VBox {
    
    private final DoubleProperty value = new SimpleDoubleProperty(0);
    private final DoubleProperty min = new SimpleDoubleProperty(0);
    private final DoubleProperty max = new SimpleDoubleProperty(100);
    private final StringProperty displayName = new SimpleStringProperty("");
    
    private final Label nameLabel;
    private final Label valueLabel;
    
    public ValueDisplay() {
        setSpacing(5);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        
        // Name label
        nameLabel = new Label();
        nameLabel.getStyleClass().add("value-display-name");
        nameLabel.textProperty().bind(displayName);
        
        // Virtual display box
        HBox displayBox = new HBox();
        displayBox.setAlignment(Pos.CENTER);
        displayBox.getStyleClass().add("value-display-box");
        displayBox.setPadding(new Insets(15, 20, 15, 20));
        displayBox.setMinWidth(120);
        
        // Value label
        valueLabel = new Label();
        valueLabel.getStyleClass().add("value-display-value");
        
        displayBox.getChildren().add(valueLabel);
        getChildren().addAll(nameLabel, displayBox);
        
        value.addListener((obs, oldVal, newVal) -> updateDisplay());
        
        updateDisplay();
    }
    
    private void updateDisplay() {
        valueLabel.setText(String.format("%.0f", value.get()));
    }
    
    // Property accessors
    public double getValue() {
        return value.get();
    }
    
    public void setValue(double value) {
        this.value.set(value);
    }
    
    public DoubleProperty valueProperty() {
        return value;
    }
    
    public double getMin() {
        return min.get();
    }
    
    public void setMin(double min) {
        this.min.set(min);
    }
    
    public DoubleProperty minProperty() {
        return min;
    }
    
    public double getMax() {
        return max.get();
    }
    
    public void setMax(double max) {
        this.max.set(max);
    }
    
    public DoubleProperty maxProperty() {
        return max;
    }
    
    public String getDisplayName() {
        return displayName.get();
    }
    
    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }
    
    public StringProperty displayNameProperty() {
        return displayName;
    }
}
