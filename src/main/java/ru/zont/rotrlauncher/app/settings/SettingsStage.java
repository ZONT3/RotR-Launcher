package ru.zont.rotrlauncher.app.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.zont.rotrlauncher.app.AppCommons;

import java.io.IOException;

public class SettingsStage extends Stage {

    private final Parent root;
    private final Scene scene;

    private double xOffset;
    private double yOffset;

    private final SettingsStageController controller;

    private GameSettingsWindow gameSettings;

    public SettingsStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/settings.fxml"));
            root = loader.load();
            scene = new Scene(root);
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        scene.setFill(Color.TRANSPARENT);
        setResizable(false);
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Settings");
        getIcons().add(new Image(getClass().getResourceAsStream("/pic/rotr.png")));

        setDraggable();
        setupOnActions();

        setScene(scene);

        setupWindows();
    }

    private void setupWindows() {
        gameSettings = new GameSettingsWindow();
        setWindow(gameSettings);
    }

    private void setupOnActions() {
        controller.btn_close.setOnAction(e -> AppCommons.fadeOut(root, ee -> close()));

        setOnShowing(n -> AppCommons.fadeIn(root, null));
    }

    private void setDraggable() {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            setX(event.getScreenX() - xOffset);
            setY(event.getScreenY() - yOffset);
        });
    }

    public void setWindow(SettingsWindow window) {
        controller.window.getChildren().clear();
        controller.window.getChildren().addAll(window.getList());
    }
}
