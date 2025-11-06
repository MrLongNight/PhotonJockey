package io.github.mrlongnight.photonjockey.ui.util;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * Helper class to enable drag-and-drop reordering of tabs in a TabPane.
 */
public class TabDragHelper {

    private static final Logger logger = LoggerFactory.getLogger(TabDragHelper.class);
    private static final DataFormat TAB_FORMAT = new DataFormat("application/x-tab");

    /**
     * Enables drag-and-drop reordering for all tabs in the given TabPane.
     * 
     * @param tabPane the TabPane to enable drag-and-drop on
     */
    public static void enableTabDragAndDrop(TabPane tabPane) {
        if (tabPane == null) {
            logger.warn("Cannot enable drag-and-drop on null TabPane");
            return;
        }

        for (Tab tab : tabPane.getTabs()) {
            enableDragForTab(tab, tabPane);
        }

        // Listen for new tabs being added
        tabPane.getTabs().addListener((javafx.collections.ListChangeListener.Change<? extends Tab> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Tab tab : change.getAddedSubList()) {
                        enableDragForTab(tab, tabPane);
                    }
                }
            }
        });

        logger.info("Drag-and-drop enabled for TabPane with {} tabs", tabPane.getTabs().size());
    }

    private static void enableDragForTab(Tab tab, TabPane tabPane) {
        // We need to wait for the tab to be rendered before we can access its graphic node
        javafx.application.Platform.runLater(() -> {
            // Find the tab's header node
            Pane tabNode = findTabNode(tabPane, tab);
            if (tabNode == null) {
                logger.debug("Could not find tab node for tab: {}", tab.getText());
                return;
            }

            // Enable drag on the tab
            tabNode.setOnDragDetected(event -> {
                Dragboard dragboard = tabNode.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(TAB_FORMAT, tab.getText());
                dragboard.setContent(content);
                event.consume();
                logger.debug("Drag started for tab: {}", tab.getText());
            });

            tabNode.setOnDragOver(event -> {
                if (event.getGestureSource() != tabNode && event.getDragboard().hasContent(TAB_FORMAT)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            tabNode.setOnDragEntered(event -> {
                if (event.getGestureSource() != tabNode && event.getDragboard().hasContent(TAB_FORMAT)) {
                    tabNode.setStyle("-fx-background-color: derive(-fx-accent-purple, 50%);");
                }
                event.consume();
            });

            tabNode.setOnDragExited(event -> {
                tabNode.setStyle("");
                event.consume();
            });

            tabNode.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                boolean success = false;
                if (dragboard.hasContent(TAB_FORMAT)) {
                    String draggedTabText = (String) dragboard.getContent(TAB_FORMAT);
                    Tab draggedTab = findTabByText(tabPane, draggedTabText);
                    
                    if (draggedTab != null && draggedTab != tab) {
                        int draggedIndex = tabPane.getTabs().indexOf(draggedTab);
                        int targetIndex = tabPane.getTabs().indexOf(tab);
                        
                        // Remove and re-add at new position
                        tabPane.getTabs().remove(draggedIndex);
                        tabPane.getTabs().add(targetIndex, draggedTab);
                        
                        // Keep the dragged tab selected
                        tabPane.getSelectionModel().select(draggedTab);
                        
                        success = true;
                        logger.info("Tab '{}' moved to position {}", draggedTabText, targetIndex);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            tabNode.setOnDragDone(event -> {
                tabNode.setStyle("");
                event.consume();
            });
        });
    }

    private static Pane findTabNode(TabPane tabPane, Tab tab) {
        // Try to find the tab header node in the scene graph
        // This is a bit hacky but necessary since JavaFX doesn't provide direct access
        return tabPane.lookupAll(".tab").stream()
            .filter(node -> node instanceof Pane)
            .map(node -> (Pane) node)
            .filter(pane -> {
                // Check if this pane corresponds to our tab
                // We'll use the tab's position as a heuristic
                int index = tabPane.getTabs().indexOf(tab);
                var tabs = tabPane.lookupAll(".tab").stream()
                        .filter(n -> n instanceof Pane)
                        .collect(Collectors.toList());
                return tabs.indexOf(pane) == index;
            })
            .findFirst()
            .orElse(null);
    }

    private static Tab findTabByText(TabPane tabPane, String text) {
        return tabPane.getTabs().stream()
            .filter(tab -> tab.getText().equals(text))
            .findFirst()
            .orElse(null);
    }
}
