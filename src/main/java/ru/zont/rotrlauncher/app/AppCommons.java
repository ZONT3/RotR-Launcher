package ru.zont.rotrlauncher.app;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

public class AppCommons {
    public static void wrapErrors(WRunnable r) {
        wrapErrors(r, null);
    }

    public static void wrapErrors(WRunnable r, String err) {
        try {
            r.run();
        } catch (Throwable e) {
            reportError(err, e);
        }
    }

    public static void wrapErrorsAsync(WRunnable r) {
        wrapErrorsAsync(r, null);
    }

    public static void wrapErrorsAsync(WRunnable r, String err) {
        new Thread(() -> wrapErrors(r, err)).start();
    }

    public static Consumer<Throwable> onErrorWrapper() {
        return throwable -> reportError(null, throwable);
    }

    private static void reportError(String err, Throwable e) {
        Platform.runLater(() -> {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String st = sw.toString();
            System.err.println(st);


            new ErrorDialog(
                    err != null ? err : Strings.STR.getString("err"),
                    st
            ).show();
        });
    }

    public static void fadeIn(Node node, EventHandler<ActionEvent> onFinish) {
        fade(node, onFinish, 0f, 1f);
    }

    public static void fadeOut(Node node, EventHandler<ActionEvent> onFinish) {
        fade(node, onFinish, 1f, 0f);
    }

    private static void fade(Node node, EventHandler<ActionEvent> onFinish, float start, float finish) {
        ObjectProperty<Float> value = new SimpleObjectProperty<>();
        KeyFrame[] keyValues = {
                new KeyFrame(Duration.ZERO, new KeyValue(value, start)),
                new KeyFrame(Duration.millis(100), new KeyValue(value, finish))
        };
        Timeline timeline = new Timeline(keyValues);
        value.addListener((observable, oldValue, newValue) -> node.setStyle("-fx-opacity: " + newValue));
        if (onFinish != null) timeline.setOnFinished(onFinish);
        timeline.playFromStart();
    }

    public interface WRunnable {
        void run() throws Throwable;
    }
}
