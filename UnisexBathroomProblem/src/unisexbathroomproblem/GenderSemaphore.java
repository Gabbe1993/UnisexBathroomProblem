package unisexbathroomproblem;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Gabriel
 */
public class GenderSemaphore extends Semaphore {

    int men = 0;
    int women = 0;

    public GenderSemaphore(int num) {
        super(num);
    }

    public void acquire(Worker worker) {
        try {
            super.acquire();
            if (worker instanceof Man) {
                men++;
            } else {
                women++;
            }
        } catch (InterruptedException ex) {
        }
    }

    public void release(Worker worker) {
        super.release();
        if (worker instanceof Man) {
            men--;
        } else {
            women--;
        }
    }
}
