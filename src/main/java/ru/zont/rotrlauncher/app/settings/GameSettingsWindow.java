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

        public static <T extends SettingsPane> T newEntry(T pane, String prefix, String property, Object defaultValue) {
            return new Entry<T>(pane, prefix, property, defaultValue).getNode();
        }

        private final String prefix;
        private final T pane;
        private final String property;
        private final Consumer<Object> valueSetter;
        private final Callback<String, Object> valueCaster;
        private final DeferredTask<Object> dt = new DeferredTask<>(2000, this::storeValue);

        private Entry(T pane, String prefix, String property, Object defaultValue) {
            this.pane = pane;
            this.property = property;
            this.prefix = prefix;

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
            Config.storeSetting(prefix, property, value);
        }

        private void initValue(Object def) {
            String p = Config.getSetting(prefix, property);
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
            Entry.newEntry(new CCheckBox("settings.game.skipIntro"), Config.PREFIX_GAME, "skipIntro", Boolean.TRUE),
            Entry.newEntry(new CCheckBox("settings.game.noSplash"), Config.PREFIX_GAME, "noSplash", Boolean.TRUE),
            Entry.newEntry(new CCheckBox("settings.game.enableHT"), Config.PREFIX_GAME, "enableHT", Boolean.TRUE),
            Entry.newEntry(new CCheckBox("settings.game.hugePages"), Config.PREFIX_GAME, "hugePages", Boolean.TRUE),
            new CSection(STR.getString("settings.launcher.sect")),
            Entry.newEntry(new CCheckBox("settings.launcher.close"), Config.PREFIX_LAUNCHER, "close", Boolean.TRUE)
        );
    }
}
