package ru.zont.rotrlauncher;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.sql.*;
import java.util.function.Consumer;

public class OnlineListener extends Thread implements ReportingError {
    private Consumer<Integer> update;
    private Consumer<Throwable> onError;

    public OnlineListener(Consumer<Integer> update) {
        super("OnlineListenerThread");
        this.update = update;
        setDaemon(true);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        try {
            while (!interrupted()) {
                update.accept(fetchPlayers());
                sleep(60000);
            }
        } catch (InterruptedException ignored) {
        } catch (Throwable e) {
            onError.accept(e);
        }
    }

    private int fetchPlayers() {
        try (Connection connection = DriverManager.getConnection(Commons.DB_CONNECTION);
             Statement st = connection.createStatement()) {
            ResultSet set = st.executeQuery("SELECT sv_players FROM serverinf WHERE sv_port='2302'");
            if (!set.next()) throw new RuntimeException("Cannot fetch server status");
            String string = set.getString(1);
            JsonElement jsonElement = JsonParser.parseString(string);
            if (!jsonElement.isJsonArray()) throw new IllegalStateException("Malformed players row");
            return jsonElement.getAsJsonArray().size();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setOnError(Consumer<Throwable> onError) {
        this.onError = onError;
    }
}
