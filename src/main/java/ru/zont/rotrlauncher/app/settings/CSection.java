package ru.zont.rotrlauncher.app.settings;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;

public class CSection extends Label {
    public CSection(String str) {
        super(str);
        ObservableList<String> styleClass = getStyleClass();
        styleClass.add("settings-window");
        styleClass.add("settings-window-section");
        setMinHeight(45.0);
    }
}
