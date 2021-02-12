package ru.zont.rotrlauncher.app;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class FadeInSkin extends ButtonSkin {
    public FadeInSkin(Button control) {
        super(control);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
        fadeIn.setNode(control);
        fadeIn.setToValue(1);
        control.setOnMouseEntered(e -> fadeIn.playFromStart());

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        fadeOut.setNode(control);
        fadeOut.setToValue(0.5);
        control.setOnMouseExited(e -> fadeOut.playFromStart());

        control.setOpacity(0.5);
    }

}