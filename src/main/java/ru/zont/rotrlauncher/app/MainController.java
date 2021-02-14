package ru.zont.rotrlauncher.app;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    public AnchorPane root;
    public Button btn_close;
    public ImageView background;
    public Button btn_mini;
    public Button btn_settings;
    public Button btn_vk;
    public Button btn_dis;
    public Button btn_connect;
    public Button btn_locate;
    public Label dir;
    public Text online;
    public Text online_players;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}