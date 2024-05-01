package com.satveerbrar.crm;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class ButtonUtils {
    public static void setHoverCursor(Button button) {
        button.setOnMouseEntered(e -> {
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(e -> {
            button.setCursor(Cursor.DEFAULT);
        });
    }
    public static void setHoverCursor(Label label) {
        label.setOnMouseEntered(e -> {
            label.setCursor(Cursor.HAND);
        });

        label.setOnMouseExited(e -> {
            label.setCursor(Cursor.DEFAULT);
        });
    }
}
