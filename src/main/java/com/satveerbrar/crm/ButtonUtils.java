package com.satveerbrar.crm;

import javafx.scene.Cursor;
import javafx.scene.Node;

public class ButtonUtils {
  public static void setHoverCursor(Node node) {
    node.setOnMouseEntered(e -> node.setCursor(Cursor.HAND));
    node.setOnMouseExited(e -> node.setCursor(Cursor.DEFAULT));
  }
}
