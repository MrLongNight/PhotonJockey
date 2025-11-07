package io.github.mrlongnight.photonjockey.ui.layout;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * A container that allows panels to be rearranged via drag-and-drop.
 */
public class DraggableLayoutContainer extends FlowPane {
    
    private static final Logger logger = LoggerFactory.getLogger(DraggableLayoutContainer.class);
    private static final Gson gson = new Gson();
    
    private final Config config;
    private final Map<String, DraggablePanel> panels = new LinkedHashMap<>();
    private boolean layoutCustomizationEnabled = false;
    
    public DraggableLayoutContainer(Config config) {
        this.config = config;
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10));
        
        loadLayoutConfiguration();
    }
    
    /**
     * Adds a panel to the container.
     */
    public void addPanel(String panelId, String panelTitle, javafx.scene.Node content) {
        DraggablePanel panel = new DraggablePanel(panelId, panelTitle, content);
        panels.put(panelId, panel);
        getChildren().add(panel);
        
        // Apply saved visibility
        Boolean visible = getPanelVisibility(panelId);
        if (visible != null) {
            panel.setVisible(visible);
        }
    }
    
    /**
     * Enables or disables layout customization mode.
     */
    public void setLayoutCustomizationEnabled(boolean enabled) {
        this.layoutCustomizationEnabled = enabled;
        
        // Update visual feedback
        if (enabled) {
            getStyleClass().add("layout-customization-active");
        } else {
            getStyleClass().remove("layout-customization-active");
        }
    }
    
    /**
     * Called when a panel is dropped after dragging.
     */
    public void handlePanelDrop(DraggablePanel panel) {
        if (!layoutCustomizationEnabled) {
            // Reset position if customization is disabled
            panel.setTranslateX(0);
            panel.setTranslateY(0);
            return;
        }
        
        // Find the closest drop target position
        int currentIndex = getChildren().indexOf(panel);
        int newIndex = findDropTargetIndex(panel);
        
        if (newIndex >= 0 && newIndex != currentIndex) {
            getChildren().remove(panel);
            getChildren().add(newIndex, panel);
        }
        
        // Reset translation
        panel.setTranslateX(0);
        panel.setTranslateY(0);
        
        saveLayoutConfiguration();
    }
    
    private int findDropTargetIndex(DraggablePanel draggedPanel) {
        double panelCenterX = draggedPanel.localToScene(draggedPanel.getWidth() / 2, 0).getX();
        double panelCenterY = draggedPanel.localToScene(0, draggedPanel.getHeight() / 2).getY();
        
        int closestIndex = -1;
        double closestDistance = Double.MAX_VALUE;
        
        for (int i = 0; i < getChildren().size(); i++) {
            javafx.scene.Node node = getChildren().get(i);
            if (node == draggedPanel) {
                continue;
            }
            
            double nodeCenterX = node.localToScene(node.getBoundsInLocal().getWidth() / 2, 0).getX();
            double nodeCenterY = node.localToScene(0, node.getBoundsInLocal().getHeight() / 2).getY();
            
            double distance = Math.hypot(panelCenterX - nodeCenterX, panelCenterY - nodeCenterY);
            
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }
        
        return closestIndex;
    }
    
    /**
     * Saves the current layout configuration to config.
     */
    private void saveLayoutConfiguration() {
        try {
            // Save panel order
            List<String> panelOrder = new ArrayList<>();
            for (javafx.scene.Node node : getChildren()) {
                if (node instanceof DraggablePanel) {
                    panelOrder.add(((DraggablePanel) node).getPanelId());
                }
            }
            
            String orderJson = gson.toJson(panelOrder);
            config.put(ConfigNode.UI_LAYOUT_CUSTOMIZATION, orderJson);
            
            // Save panel visibility
            Map<String, Boolean> visibility = new HashMap<>();
            for (DraggablePanel panel : panels.values()) {
                visibility.put(panel.getPanelId(), panel.isVisible());
            }
            
            String visibilityJson = gson.toJson(visibility);
            config.put(ConfigNode.UI_PANEL_VISIBILITY, visibilityJson);
            
            logger.info("Layout configuration saved");
        } catch (Exception e) {
            logger.error("Failed to save layout configuration", e);
        }
    }
    
    /**
     * Loads the layout configuration from config.
     */
    private void loadLayoutConfiguration() {
        try {
            String orderJson = config.get(ConfigNode.UI_LAYOUT_CUSTOMIZATION);
            if (orderJson != null && !orderJson.isEmpty()) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> panelOrder = gson.fromJson(orderJson, listType);
                
                // Reorder panels according to saved configuration
                if (panelOrder != null) {
                    applyPanelOrder(panelOrder);
                }
            }
            
            logger.info("Layout configuration loaded");
        } catch (Exception e) {
            logger.error("Failed to load layout configuration", e);
        }
    }
    
    /**
     * Applies a specific panel order.
     */
    private void applyPanelOrder(List<String> panelOrder) {
        List<javafx.scene.Node> reorderedPanels = new ArrayList<>();
        
        for (String panelId : panelOrder) {
            DraggablePanel panel = panels.get(panelId);
            if (panel != null) {
                reorderedPanels.add(panel);
            }
        }
        
        // Add any panels not in the saved order
        for (javafx.scene.Node node : getChildren()) {
            if (!reorderedPanels.contains(node)) {
                reorderedPanels.add(node);
            }
        }
        
        getChildren().setAll(reorderedPanels);
    }
    
    /**
     * Gets the visibility state of a panel.
     */
    private Boolean getPanelVisibility(String panelId) {
        try {
            String visibilityJson = config.get(ConfigNode.UI_PANEL_VISIBILITY);
            if (visibilityJson != null && !visibilityJson.isEmpty()) {
                Type mapType = new TypeToken<Map<String, Boolean>>() {}.getType();
                Map<String, Boolean> visibility = gson.fromJson(visibilityJson, mapType);
                return visibility != null ? visibility.get(panelId) : null;
            }
        } catch (Exception e) {
            logger.error("Failed to get panel visibility for " + panelId, e);
        }
        return null;
    }
    
    /**
     * Resets the layout to default.
     */
    public void resetLayout() {
        config.remove(ConfigNode.UI_LAYOUT_CUSTOMIZATION);
        config.remove(ConfigNode.UI_PANEL_VISIBILITY);
        
        // Show all panels
        for (DraggablePanel panel : panels.values()) {
            panel.setVisible(true);
            panel.setCollapsed(false);
        }
        
        logger.info("Layout reset to default");
    }
    
    /**
     * Gets all panels in the container.
     */
    public Collection<DraggablePanel> getPanels() {
        return panels.values();
    }
}
