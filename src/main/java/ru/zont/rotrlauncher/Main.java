package ru.zont.rotrlauncher;

import java.io.File;
import java.sql.*;
import java.util.function.Consumer;

public class Main extends Thread implements ReportingError {
    private boolean done;
    private Runnable onDone = null;
    private Runnable onX86Warning = null;
    private Consumer<Throwable> onError = null;

    public Main() {
        super("LauncherMain");
        done = false;
    }

    @Override
    public void run() {
        try {
            int actualVersion = getActualVersion();
            if (actualVersion == 0) throw new RuntimeException("Malformed response from DB");
            if (Mods.getVersion() != actualVersion)
                throw new ModsOutdatedException();

            File armaDir = Config.getArmaDir();
            File exe = findExe(armaDir);
            String params = buildParams();

            System.out.println("Starting arma with params: " + params);
            Runtime.getRuntime().exec(String.format("cmd /c start %s %s", exe.getName(), params), null, armaDir).waitFor();
        } catch (Throwable e) {
            if (onError != null)
                onError.accept(e);
            else e.printStackTrace();
        } finally {
            done();
        }
    }

    private File findExe(File armaDir) {
        if (armaDir == null || !Commons.isArmaDir(armaDir.getAbsolutePath()))
            throw new IllegalStateException("Invalid Arma 3 path!");

        File file = new File(armaDir, "arma3_x64.exe");
        if (!file.isFile()) {
            file = new File(armaDir, "arma3.exe");
            if (!file.isFile())
                throw new IllegalStateException("Cloud not find Arma 3 executable");
            else if (onX86Warning != null)
                onX86Warning.run();
        }

        return file;
    }

    private String buildParams() {
        StringBuilder builder = new StringBuilder();
        if (Config.getSettingB(Config.PREFIX_GAME, "skipIntro", true))
            builder.append(" ").append("-skipIntro");
        if (Config.getSettingB(Config.PREFIX_GAME, "noSplash", true))
            builder.append(" ").append("-noSplash");
        if (Config.getSettingB(Config.PREFIX_GAME, "enableHT", true))
            builder.append(" ").append("-enableHT");
        if (Config.getSettingB(Config.PREFIX_GAME, "hugePages", true))
            builder.append(" ").append("-hugePages");

        builder.append(" ")
                .append("\"-mod=")
                .append(wrapMods(Mods.getMods()))
                .append("\"");

        return builder.toString().trim();
    }

    private void done() {
        done = true;
        if (onDone != null)
            onDone.run();
    }

    private String wrapMods(String mods) {
        File dir = new File(System.getProperty("user.dir"), "mods");
        StringBuilder sb = new StringBuilder();
        for (String mod: mods.split(";"))
            sb.append(new File(dir, mod).getAbsolutePath()).append(";");
        return sb.toString();
    }

    private int getActualVersion() {
        try (Connection connection = DriverManager.getConnection(Commons.DB_CONNECTION);
             Statement st = connection.createStatement()) {
            ResultSet set = st.executeQuery("SELECT sv_mods_ver FROM serverinf WHERE sv_port='2302'");
            if (!set.next()) throw new RuntimeException("Cannot fetch server status");
            return set.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setOnX86Warning(Runnable onX86Warning) {
        this.onX86Warning = onX86Warning;
    }

    public void setOnDone(Runnable onDone) {
        this.onDone = onDone;
    }

    @Override
    public void setOnError(Consumer<Throwable> onError) {
        this.onError = onError;
    }
}
