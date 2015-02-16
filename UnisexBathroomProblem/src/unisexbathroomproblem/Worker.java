package unisexbathroomproblem;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class Worker {

    long arrivedTime, workTime, bathroomTime;
    Bathroom bathroom;
    Random rand = new Random();

    public Worker(Bathroom bathroom) {
        this.bathroom = bathroom;

        this.arrivedTime = System.currentTimeMillis();
        this.workTime = rand.nextInt();
        this.bathroomTime = rand.nextInt();
        
        work();
    }

    public void useBathroom() {
        try {
            Thread.sleep(bathroomTime);
            bathroomTime = rand.nextLong();
            work();
        } catch (InterruptedException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void work() {
        try {
            Thread.sleep(workTime);
            bathroom.placeInQueue(this);
        } catch (InterruptedException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setBathroomTime(long bathroomTime) {
        this.bathroomTime = bathroomTime;
    }
}
