package ru.zont.rotrlauncher.app.settings;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public class CTextField extends SettingsPane {

    private final TextField textField;

    public CTextField(String title) {
        super(title);

        textField = new TextField();
        textField.setMaxWidth(280);
        ObservableList<String> styleClass = textField.getStyleClass();
        styleClass.add("settings-window");
        styleClass.add("settings-window-textfield");

        add(textField, 1, 0);
    }

    public TextField getTextField() {
        return textField;
    }
}