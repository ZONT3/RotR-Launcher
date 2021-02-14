package ru.zont.rotrlauncher.app.settings;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class SettingsPane extends GridPane {
    private final Label titleLabel;

    public SettingsPane(String title) {
        ColumnConstraints columnTitle = new ColumnConstraints();
        columnTitle.setMinWidth(50);
        columnTitle.setPercentWidth(40);
        columnTitle.setHgrow(Priority.SOMETIMES);

        ColumnConstraints columnValue = new ColumnConstraints();
        columnValue.setMinWidth(50);
        columnValue.setHgrow(Priority.SOMETIMES);

        RowConstraints row = new RowConstraints();
        row.setMinHeight(45);
        row.setPrefHeight(45);
        row.setVgrow(Priority.SOMETIMES);

        ObservableList<ColumnConstraints> columnConstraints = getColumnConstraints();
        columnConstraints.add(columnTitle);
        columnConstraints.add(columnValue);
        getRowConstraints().add(row);

        titleLabel = new Label(title);
        setMargin(titleLabel, new Insets(0,5,0,0));
        titleLabel.getStyleClass().add("settings-window");

        add(titleLabel, 0, 0);
    }

    public Label getTitleLabel() {
        return titleLabel;
    }
}
