package ru.zont.rotrlauncher;

import java.util.function.Consumer;

public interface ReportingError {
    void setOnError(Consumer<Throwable> onError);
}
