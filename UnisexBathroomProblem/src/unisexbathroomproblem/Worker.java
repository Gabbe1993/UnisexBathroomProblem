package unisexbathroomproblem;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class Worker implements Runnable {

    int bathroomTime, id;
    String info;
    private Bathroom bathroom;
    private Random rand = new Random();

    public Worker(Bathroom bathroom, int id, char gender) {
        this.bathroom = bathroom;
        this.info = gender + "(" + id + ")";
    }

    @Override
    public void run() {
        System.out.println("Started " + info);
        work();
    }

    public void useBathroom() {
        bathroomTime = rand.nextInt(10000);
        boolean usedBathroom = false;

        try {
            Bathroom.sem.acquire(this);

            while (!usedBathroom) {
                Thread.sleep(3000);

                if (bathroom.allowed(this)) {
                    usedBathroom = true;
                    bathroom.useBathroom(this);
                    Bathroom.sem.release(this);
                    Thread.sleep(bathroomTime);
                }
            }
            work();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void work() {
        int workTime = rand.nextInt(60000);

        try {
            //System.out.println(info + " working for: " + workTime / 1000 + " sek");
            Thread.sleep(workTime);
            useBathroom();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
