package ru.zont.rotrlauncher.app;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.zont.rotrlauncher.*;

import java.awt.*;
import java.io.File;
import java.net.URI;

import static ru.zont.rotrlauncher.app.Strings.STR;

public class Application extends javafx.application.Application {

    private double xOffset = 0;
    private double yOffset = 0;

    private static Main mainInst = null;
    private MainController controller;
    private Stage primaryStage;
    private OnlineListener onlineListener;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        Scene scene = new Scene(loader.load());

        controller = loader.getController();
        primaryStage = stage;

        scene.setFill(Color.TRANSPARENT);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Revenge of the Republic");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/pic/rotr.png")));

        setDraggable();
        setupOnActions();

        primaryStage.setScene(scene);
        primaryStage.show();

        checkOptions();
        updPath(getArmaDir());

        setupHandlers();
    }

    private void setupHandlers() {
        onlineListener = new OnlineListener(i -> Platform.runLater(() -> {
            controller.online.setText(String.format("%02d", i));
            controller.online_players.setText(" " + Strings.countPlayers(i));
        }));
        onlineListener.setOnError(Commons.onErrorWrapper());
        onlineListener.start();
    }

    private void setupOnActions() {
        controller.btn_connect.setOnAction(event -> connect());
        controller.btn_close.setOnAction(event -> primaryStage.close());
        controller.btn_locate.setOnAction(event ->
                Commons.wrapErrors(this::chooseArmaDir) );
        controller.btn_mini.setOnAction(event -> primaryStage.setIconified(true));
        controller.btn_vk.setOnAction(event ->
                Commons.wrapErrors(() -> Desktop.getDesktop().browse(URI.create("https://vk.com/revenge_of_the_republic"))) );
        controller.btn_dis.setOnAction(event ->
                Commons.wrapErrors(() -> Desktop.getDesktop().browse(URI.create("https://discord.gg/MYHY27DrSQ"))) );
    }

    private void setDraggable() {
        controller.root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        controller.root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (onlineListener != null)
            onlineListener.interrupt();
    }

    private void connect() {
        if (mainInst != null && !mainInst.isDone()) return;
        Commons.wrapErrors(() -> {
            controller.btn_connect.setDisable(true);
            mainInst = new Main();
            mainInst.setOnDone(() -> controller.btn_connect.setDisable(false));
            mainInst.setOnX86Warning(() -> Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, STR.getString("warn.x86")).show()));
            mainInst.setOnError(throwable -> {
                if (throwable instanceof ModsOutdatedException) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, STR.getString("main.outdated"));
                        alert.setHeaderText(STR.getString("main.outdated.title"));
                        alert.show();
                    });
                } else Commons.onErrorWrapper().accept(throwable);
            });
            mainInst.start();
        }, STR.getString("err.start"));
    }

    private void chooseArmaDir() {
        File file = null;
        File curr = Options.getArmaDir();
        do {

            DirectoryChooser fc = new DirectoryChooser();

            if (file != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, STR.getString("main.fc.wrong"));
                alert.setHeaderText("");
                alert.show();
                fc.setInitialDirectory(file);
            } else if (curr != null && Commons.isArmaDir(curr.getAbsolutePath()))
                fc.setInitialDirectory(curr);
            else {
                File programFiles = new File("/Program Files (x86)");
                if (programFiles.isDirectory())
                    fc.setInitialDirectory(programFiles);
            }

            fc.setTitle(STR.getString("main.fc.title"));
            file = fc.showDialog(this.primaryStage);

        } while (file != null && !Commons.isArmaDir(file.getAbsolutePath()));

        if (file != null) {
            Options.setArmaDir(file.getAbsolutePath());
            updPath(Options.getArmaDir());
        }
    }

    private void updPath(File armaDir) {
        if (armaDir == null) {
            controller.dir.setText(STR.getString("main.dir.not_stated"));
            controller.dir.getStyleClass().add("dir-corrupted");
            controller.btn_connect.setDisable(true);
        } else {
            controller.dir.setText(armaDir.getAbsolutePath());
            controller.dir.getStyleClass().remove("dir-corrupted");
            controller.btn_connect.setDisable(false);
        }
    }

    private File getArmaDir() {
        File armaDir = Options.getArmaDir();
        if (armaDir == null) {
            String dir = seekArmaDir();
            if (dir != null) {
                File file = new File(dir);
                Options.setArmaDir(file.getAbsolutePath());
                armaDir = file;
            }
        }
        if (armaDir != null && !Commons.isArmaDir(armaDir.getAbsolutePath())) {
            armaDir = null;
            Options.setArmaDir("");
        }
        return armaDir;
    }

    private String seekArmaDir() {
        String[] winDirs = {
                "/Program Files (x86)/Steam/steamapps/common/Arma 3",
                "/Program Files/Steam/steamapps/common/Arma 3"
        };
        String[] unixDirs = {
                "~/.local/share/Steam/steamapps/common/Arma 3",
                "~/.steam/steam/SteamApps/common/Arma 3"
        };
        String[] macDirs = {
                "~/Library/Application Support/Steam/steamapps/common/Arma 3"
        };

        String[] dirs = {};
        if (Commons.isWindows())
            dirs = winDirs;
        else if (Commons.isUnix())
            dirs = unixDirs;
        else if (Commons.isMac())
            dirs = macDirs;

        for (String dir: dirs)
            if (Commons.isArmaDir(dir))
                return dir;
        return null;
    }

    private void checkOptions() {
        if (Options.optFile == null) {
            String err = Options.optError != null
                    ? String.format("%s: %s",
                            Options.optError.getClass().getSimpleName(),
                            Options.optError.getLocalizedMessage() )
                    : "";
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format(STR.getString("err.opts"), err));
            alert.setHeaderText(STR.getString("err"));
            alert.show();
            return;
        }

        Options.initOptions();
    }
}
