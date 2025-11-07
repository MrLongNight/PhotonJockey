package io.github.mrlongnight.photonjockey.ui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * A rotary knob control for value selection.
 */
public class RotaryKnob extends Region {
    
    private static final double DEFAULT_SIZE = 80;
    private static final double MIN_ANGLE = 45;  // degrees from bottom
    private static final double MAX_ANGLE = 315; // degrees from bottom
    
    private final Canvas canvas;
    private final DoubleProperty value = new SimpleDoubleProperty(0);
    private final DoubleProperty min = new SimpleDoubleProperty(0);
    private final DoubleProperty max = new SimpleDoubleProperty(100);
    
    private Point2D dragStart;
    private double dragStartValue;
    
    public RotaryKnob() {
        canvas = new Canvas(DEFAULT_SIZE, DEFAULT_SIZE);
        getChildren().add(canvas);
        
        setPrefSize(DEFAULT_SIZE, DEFAULT_SIZE);
        setMinSize(DEFAULT_SIZE, DEFAULT_SIZE);
        setMaxSize(DEFAULT_SIZE, DEFAULT_SIZE);
        
        setupMouseHandlers();
        
        value.addListener((obs, oldVal, newVal) -> draw());
        min.addListener((obs, oldVal, newVal) -> draw());
        max.addListener((obs, oldVal, newVal) -> draw());
        
        draw();
    }
    
    private void setupMouseHandlers() {
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setCursor(javafx.scene.Cursor.HAND);
    }
    
    private void handleMousePressed(MouseEvent event) {
        dragStart = new Point2D(event.getX(), event.getY());
        dragStartValue = value.get();
    }
    
    private void handleMouseDragged(MouseEvent event) {
        if (dragStart == null) {
            return;
        }
        
        double dy = dragStart.getY() - event.getY();
        double range = max.get() - min.get();
        double deltaValue = (dy / canvas.getHeight()) * range;
        
        double newValue = Math.max(min.get(), Math.min(max.get(), dragStartValue + deltaValue));
        value.set(newValue);
    }
    
    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        // Clear canvas
        gc.clearRect(0, 0, width, height);
        
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 5;
        
        // Draw outer circle
        gc.setStroke(Color.web("#444444"));
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        // Draw inner filled circle (knob body)
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillOval(centerX - radius * 0.9, centerY - radius * 0.9, 
                    radius * 1.8, radius * 1.8);
        
        // Calculate indicator angle
        double normalizedValue = (value.get() - min.get()) / (max.get() - min.get());
        double angle = MIN_ANGLE + normalizedValue * (MAX_ANGLE - MIN_ANGLE);
        double angleRad = Math.toRadians(angle);
        
        // Draw value arc
        gc.setStroke(Color.web("#8A2BE2"));
        gc.setLineWidth(3);
        double arcStartAngle = 180 - MIN_ANGLE;
        double arcExtent = -(angle - MIN_ANGLE);
        gc.strokeArc(centerX - radius * 0.95, centerY - radius * 0.95, 
                     radius * 1.9, radius * 1.9, arcStartAngle, arcExtent, javafx.scene.shape.ArcType.OPEN);
        
        // Draw indicator line
        double indicatorLength = radius * 0.7;
        double indicatorX = centerX + Math.sin(angleRad) * indicatorLength;
        double indicatorY = centerY - Math.cos(angleRad) * indicatorLength;
        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(centerX, centerY, indicatorX, indicatorY);
        
        // Draw value text
        gc.setFill(Color.web("#cccccc"));
        gc.setFont(Font.font("System", 12));
        gc.setTextAlign(TextAlignment.CENTER);
        String valueText = String.format("%.0f", value.get());
        gc.fillText(valueText, centerX, height - 10);
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double size = Math.min(getWidth(), getHeight());
        canvas.setWidth(size);
        canvas.setHeight(size);
        draw();
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
}
