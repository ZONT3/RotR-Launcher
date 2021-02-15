package ru.zont.rotrlauncher.app.settings;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import ru.zont.rotrlauncher.Config;
import ru.zont.rotrlauncher.DeferredTask;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static ru.zont.rotrlauncher.app.Strings.STR;

public class GameSettingsWindow extends SettingsWindow {

    private static class Entry<T extends SettingsPane> {

        public static <T extends SettingsPane> T newEntry(T pane, String property, Object defaultValue) {
            return new Entry<T>(pane, property, defaultValue).getNode();
        }

        private final T pane;
        private final String property;
        private final Consumer<Object> valueSetter;
        private final Callback<String, Object> valueCaster;
        private final DeferredTask<Object> dt = new DeferredTask<>(2000, this::storeValue);

        private Entry(T pane, String property, Object defaultValue) {
            this.pane = pane;
            this.property = property;

            if (pane instanceof CCheckBox) {
                CCheckBox cb = (CCheckBox) pane;
                CheckBox checkBox = cb.getCheckBox();
                checkBox.setOnAction(e -> storeValue(checkBox.isSelected()));
                valueSetter = o -> checkBox.setSelected((Boolean)o);
                valueCaster = "true"::equals;
            } else if (pane instanceof CTextField) {
                CTextField tf = (CTextField) pane;
                TextField textField = tf.getTextField();
                textField.textProperty().addListener((observable, oldValue, newValue) -> dt.updateValue(newValue));
                valueSetter = o -> textField.setText((String)o);
                valueCaster = o -> o;
            } else throw new IllegalArgumentException("Unknown SettingsPane subclass");

            initValue(defaultValue);
        }

        private void storeValue(Object value) {
            Config.storeSetting(Config.PREFIX_GAME, property, value);
        }

        private void initValue(Object def) {
            String p = Config.getSetting(Config.PREFIX_GAME, property);
            if (p == null) {
                valueSetter.accept(def);
                storeValue(def);
            } else valueSetter.accept(valueCaster.call(p));
        }

        public T getNode() {
            return pane;
        }
    }

    @Override
    public List<Node> getList() {
        return Arrays.asList(
            new CSection(STR.getString("settings.game.sect.performance")),
            Entry.newEntry(new CCheckBox("settings.game.skipIntro"), "skipIntro", Boolean.TRUE),
            Entry.newEntry(new CCheckBox("settings.game.noSplash"), "noSplash", Boolean.TRUE),
            Entry.newEntry(new CCheckBox("settings.game.enableHT"), "enableHT", Boolean.TRUE),
            Entry.newEntry(new CCheckBox("settings.game.hugePages"), "hugePages", Boolean.TRUE)
        );
    }
}
