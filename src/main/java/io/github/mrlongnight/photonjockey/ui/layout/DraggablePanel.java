package io.github.mrlongnight.photonjockey.ui.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;

/**
 * A panel that can be dragged and repositioned in a layout.
 */
public class DraggablePanel extends VBox {
    
    private final String panelId;
    private final String panelTitle;
    private final Node content;
    
    private boolean isDragging = false;
    private double dragStartX;
    private double dragStartY;
    private boolean isCollapsed = false;
    
    private VBox contentContainer;
    private Button collapseButton;
    private Button hideButton;
    
    public DraggablePanel(String panelId, String panelTitle, Node content) {
        this.panelId = panelId;
        this.panelTitle = panelTitle;
        this.content = content;
        
        getStyleClass().add("draggable-panel");
        setSpacing(0);
        
        buildHeader();
        buildContent();
    }
    
    private void buildHeader() {
        HBox header = new HBox(10);
        header.getStyleClass().add("panel-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(5, 10, 5, 10));
        
        // Drag handle (icon/label)
        Label dragHandle = new Label("⋮⋮");
        dragHandle.getStyleClass().add("panel-header-label");
        dragHandle.setCursor(Cursor.MOVE);
        
        // Title
        Label titleLabel = new Label(panelTitle);
        titleLabel.getStyleClass().add("panel-header-label");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Collapse button
        collapseButton = new Button("−");
        collapseButton.getStyleClass().add("panel-control-button");
        collapseButton.setOnAction(e -> toggleCollapse());
        
        // Hide button
        hideButton = new Button("✕");
        hideButton.getStyleClass().add("panel-control-button");
        hideButton.setOnAction(e -> setVisible(false));
        
        header.getChildren().addAll(dragHandle, titleLabel, collapseButton, hideButton);
        
        // Setup drag handlers
        setupDragHandlers(header);
        
        getChildren().add(header);
    }
    
    private void buildContent() {
        contentContainer = new VBox();
        contentContainer.getChildren().add(content);
        contentContainer.setPadding(new Insets(10));
        
        getChildren().add(contentContainer);
    }
    
    private void setupDragHandlers(Node dragHandle) {
        dragHandle.setOnMousePressed(this::handleDragStart);
        dragHandle.setOnMouseDragged(this::handleDrag);
        dragHandle.setOnMouseReleased(this::handleDragEnd);
    }
    
    private void handleDragStart(MouseEvent event) {
        isDragging = true;
        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();
        getStyleClass().add("dragging");
        event.consume();
    }
    
    private void handleDrag(MouseEvent event) {
        if (!isDragging) {
            return;
        }
        
        double deltaX = event.getSceneX() - dragStartX;
        double deltaY = event.getSceneY() - dragStartY;
        
        setTranslateX(getTranslateX() + deltaX);
        setTranslateY(getTranslateY() + deltaY);
        
        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();
        
        event.consume();
    }
    
    private void handleDragEnd(MouseEvent event) {
        isDragging = false;
        getStyleClass().remove("dragging");
        
        // Notify parent layout about position change
        if (getParent() instanceof DraggableLayoutContainer) {
            ((DraggableLayoutContainer) getParent()).handlePanelDrop(this);
        }
        
        event.consume();
    }
    
    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
        contentContainer.setVisible(!isCollapsed);
        contentContainer.setManaged(!isCollapsed);
        collapseButton.setText(isCollapsed ? "+" : "−");
    }
    
    public String getPanelId() {
        return panelId;
    }
    
    public String getPanelTitle() {
        return panelTitle;
    }
    
    public boolean isCollapsed() {
        return isCollapsed;
    }
    
    public void setCollapsed(boolean collapsed) {
        if (this.isCollapsed != collapsed) {
            toggleCollapse();
        }
    }
}
