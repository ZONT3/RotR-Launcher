package ru.zont.rotrlauncher.app;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class ConnectSkin extends ButtonSkin {
    public static final int GRADIENT_TRANSLATION_TIME = 200;

    public ConnectSkin(Button control) {
        super(control);

        Timeline timeline = gradientMoveOutTimeline(control);

        control.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timeline.playFromStart();
            } else {
                timeline.setRate(-1.5);
                timeline.playFrom(Duration.millis(GRADIENT_TRANSLATION_TIME));
            }
        });
    }

    private Timeline gradientMoveOutTimeline(Button control) {
        ObjectProperty<Integer> value = new SimpleObjectProperty<>();

        KeyFrame[] keyValues = {
                new KeyFrame(Duration.ZERO, new KeyValue(value, 100)),
                new KeyFrame(Duration.millis(GRADIENT_TRANSLATION_TIME), new KeyValue(value, 0)),
        };
        Timeline timeline = new Timeline(keyValues);

        value.addListener((observable, oldValue, newValue) -> {
            if (control.isDisabled()) {
                control.setStyle("");
                timeline.stop();
                return;
            }
            control.setStyle(String.format("-fx-background-color: linear-gradient(to right, " +
                            "#0052D4 0%%, " +
                            "#4364F7 %d%%, " +
                            "#6FB1FC %d%%, " +
                            "#6FB1FC 100%%);",
                    Math.max(0, newValue - 50),
                    newValue
            ));
        });
        return timeline;
    }


}
