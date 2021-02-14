package ru.zont.rotrlauncher.app.settings;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.zont.rotrlauncher.Config;
import ru.zont.rotrlauncher.app.Strings;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsStageController implements Initializable {
    public Button btn_close;
    public Button btn_sect_game;
    public Button btn_sect_authors;
    public VBox window;
    public Label version;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        version.setText(String.format(Strings.STR.getString("name.version"), Config.getVersion()));
    }
}
