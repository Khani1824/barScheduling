package barScheduling;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Background implements Runnable {
    AtomicInteger count;


    public Background(AtomicInteger counter){
        this.count = counter;

    }

    @Override
    public void run() {
        // Just to see values
        System.out.println(count.get() + "\n");
        count.set(0);

    }

}
