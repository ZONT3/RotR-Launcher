package ru.zont.rotrlauncher.app.settings;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import static ru.zont.rotrlauncher.app.Strings.STR;

public class SettingsPane extends GridPane {
    private final Label titleLabel;
    private Tooltip tooltip = null;

    public SettingsPane(String titleRes) {
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

        titleLabel = new Label(STR.getString(titleRes));
        setMargin(titleLabel, new Insets(0,5,0,0));
        titleLabel.getStyleClass().add("settings-window");

        String key = titleRes + ".desc";
        if (STR.containsKey(key)) {
            tooltip = new Tooltip(STR.getString(key));
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(150);
            titleLabel.setTooltip(tooltip);
        }

        add(titleLabel, 0, 0);
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }
}
