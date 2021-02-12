package ru.zont.rotrlauncher;

import javafx.application.Platform;
import ru.zont.rotrlauncher.app.ErrorDialog;
import ru.zont.rotrlauncher.app.Strings;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.function.Consumer;

public class Commons {
    public static final String DB_CONNECTION = "jdbc:mysql://swarma3.online:3306/arma3sw?user=launcher&password=%40Kk1X%26B0NmjQ&serverTimezone=GMT%2B3";
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix")
                || OS.contains("nux")
                || OS.indexOf("aix") > 0);
    }

    public static boolean isArmaDir(String dir) {
        File file = new File(dir);
        if (!file.isDirectory()) return false;
        for (String s: Arrays.asList(
                "arma3.exe",
                "arma3",
                "arma3_x64.exe",
                "arma3_x64"
        )) if (new File(file, s).isFile()) return true;
        return false;
    }

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
        return throwable -> {
            reportError(null, throwable);
        };
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

    public interface WRunnable {
        void run() throws Throwable;
    }
}
