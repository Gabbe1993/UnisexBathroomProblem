package src;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Gabriel
 */
public class GenderSemaphore extends Semaphore {

    static int men = 0;
    static int women = 0;

    public GenderSemaphore(int num) {
        super(num);
    }

    public void acquire(Worker worker) {
        if (worker instanceof Man) {
            men++;
        } else {
            women++;
        }
        try {
            super.acquire();
        } catch (InterruptedException ex) {
        }
    }

    public void release(Worker worker) {
        if (worker instanceof Man) {
            men--;
        } else {
            women--;
        }
        super.release();
    }
}
