package ru.zont.rotrlauncher;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class DeferredTask<T> {
    private static long id = 0;

    private final long timeout;
    private final Consumer<T> consumer;
    private Timer timer;
    private T value;

    public DeferredTask(long timeout, Consumer<T> consumer) {
        this.timeout = timeout;
        this.consumer = consumer;
    }

    public DeferredTask(long timeout, T initialValue, Consumer<T> consumer) {
        this(timeout, consumer);
        value = initialValue;
        timer = newTimer();
    }

    private Timer newTimer() {
        final Timer timer;
        timer = new Timer("Deferred task timer #" + id++);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                consumer.accept(value);
            }
        }, timeout);
        return timer;
    }

    public void updateValue(T val) {
        value = val;
        if (timer != null)
            timer.cancel();
        timer = newTimer();
    }
}
