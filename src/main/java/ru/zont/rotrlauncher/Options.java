package ru.zont.rotrlauncher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static ru.zont.rotrlauncher.Commons.*;

public class Options {
    private static final String OPTIONS_FILENAME = "options.properties";
    private static Properties options = null;

    public static final File optFile = getOptFile();
    public static Throwable optError = null;

    private static File getOptFile() {
        try {
            File home = new File(System.getProperty("user.home"));

            File dir = null;
            if (isWindows())
                dir = new File(home, "AppData/Roaming/rotr-launcher");
            else if (isMac() || isUnix())
                dir = new File(home, ".rotr-launcher");
            else System.err.println("Cannot determine OS");
            if (dir == null) return null;

            if (!dir.mkdirs() && !dir.isDirectory()) {
                System.err.println("Cannot create properties");
                return null;
            }

            return new File(dir, OPTIONS_FILENAME);
        } catch (Throwable e) {
            optError = e;
            return null;
        }
    }

    public static void initOptions() {
        Properties def = new Properties() {{
            setProperty("a3dir", "");
            setProperty("modsDir", "");
            setProperty("modsInA3dir", "true");
        }};

        try {
            if (optFile == null) throw new IOException("File is null");
            def.load(new FileReader(optFile));
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        store(def);
        options = def;
    }

    private static void store(Properties p) {
        try {
            if (optFile == null) throw new IOException("File is null");
            p.store(new OutputStreamWriter(new FileOutputStream(optFile), StandardCharsets.UTF_8), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkState() {
        if (options == null)
            throw new IllegalStateException("Options not initialized");
    }

    private static void set(String k, String v) {
        checkState();
        options.setProperty(k, v);
        store(options);
    }

    public static File getArmaDir() {
        checkState();
        String path = options.getProperty("a3dir", "");
        if (path.isEmpty()) return null;
        return new File(path);
    }

    public static void setArmaDir(String dir) {
        set("a3dir", dir);
    }

}
