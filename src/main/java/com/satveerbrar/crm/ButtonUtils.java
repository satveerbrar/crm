package com.satveerbrar.crm;

import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * Utility class containing static method to enhance button and other interactive element behaviors
 * in JavaFX applications.
 */
public class ButtonUtils {
  /**
   * Sets the cursor to a hand cursor when hovering over the specified JavaFX node, reverting to the
   * default cursor when not hovering. This method enhances UI interactivity by indicating that the
   * node is clickable.
   *
   * @param node The node to which the hover cursor effect will be applied.
   */
  public static void setHoverCursor(Node node) {
    node.setOnMouseEntered(e -> node.setCursor(Cursor.HAND));
    node.setOnMouseExited(e -> node.setCursor(Cursor.DEFAULT));
  }
}
