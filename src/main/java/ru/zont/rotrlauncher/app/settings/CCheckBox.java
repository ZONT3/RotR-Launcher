package ru.zont.rotrlauncher.app.settings;

import javafx.scene.control.CheckBox;


public class CCheckBox extends SettingsPane {
    private final CheckBox checkBox;

    public CCheckBox(String title) {
        super(title);
        checkBox = new CheckBox();
        add(checkBox, 1, 0);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

}
