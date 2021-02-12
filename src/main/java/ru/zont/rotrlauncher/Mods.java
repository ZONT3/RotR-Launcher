package ru.zont.rotrlauncher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Mods {
    private static final File meta = new File("mods", "meta.properties");

    public static int getVersion() {
        try {
            Properties props = getProps();
            String ver = props.getProperty("version_code");
            if (ver == null)
                throw new NullPointerException("Cannot find mods version property");
            return Integer.parseInt(ver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMods() {
        try {
            Properties props = getProps();
            String mods = props.getProperty("mods");
            if (mods == null)
                throw new NullPointerException("Cannot find mods property");
            return mods;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties getProps() throws IOException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(meta);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot locate mods metadata", e);
        }
        Properties p = new Properties();
        p.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
        return p;
    }
}
