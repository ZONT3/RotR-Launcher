package ru.zont.rotrlauncher;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

            File armaDir = Options.getArmaDir();
            String params = buildParams(armaDir);

            Runtime.getRuntime().exec(String.format("cmd /c \"%s\"", params), null, armaDir);
        } catch (Throwable e) {
            if (onError != null)
                onError.accept(e);
            else e.printStackTrace();
        } finally {
            done();
        }
    }

    private void done() {
        done = true;
        if (onDone != null)
            onDone.run();
    }

    private String buildParams(File armaDir) {
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

        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(file.getAbsolutePath()).append("\"")
                .append(" ").append("-skipIntro")
                .append(" ").append("-noSplash")
                .append(" ").append("-enableHT")
                .append(" ").append("-hugePages");

        builder.append(" ")
                .append("\"-mod=")
                .append(wrapMods(Mods.getMods()))
                .append("\"");

        return builder.toString();
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
