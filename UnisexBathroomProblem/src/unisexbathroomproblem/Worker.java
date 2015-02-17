package unisexbathroomproblem;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class Worker implements Runnable {

    int bathroomTime;
    private Bathroom bathroom;
    private Random rand = new Random();

    public Worker(Bathroom bathroom) {
        this.bathroom = bathroom;
    }

    @Override
    public void run() {
        System.out.println("Started worker");
        work();
    }

    public void useBathroom() {
        bathroomTime = rand.nextInt(3000);

        try {
           // bathroom.useBathroom(this);
            System.out.println("Worker using bathroom for: " + bathroomTime / 1000 + " sek");
            Thread.sleep(bathroomTime);
            System.out.println("Worker done at bathroom!");

            work();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    private void work() {
        int workTime = rand.nextInt(10000);

        try {
            System.out.println("Worker working for: " + workTime / 1000 + " sek");
            Thread.sleep(workTime);

            bathroom.placeInQueue(this);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
