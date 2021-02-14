import ru.zont.rotrlauncher.DeferredTask;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Experiments {
    private static final DeferredTask<String> dt = new DeferredTask<>(2000, System.out::println);

    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);

        while (s.hasNext()) {
            dt.updateValue(s.nextLine());
        }
    }
}
