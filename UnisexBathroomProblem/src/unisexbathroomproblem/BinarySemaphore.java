
import java.util.concurrent.Semaphore;

public class BinarySemaphore {

    private final Semaphore countingSemaphore;

    public BinarySemaphore(int available) {
        if (available == 1) {
            countingSemaphore = new Semaphore(1, true);
        } else {
            countingSemaphore = new Semaphore(0, true);
        }
    }

    public void acquire() throws InterruptedException {
        countingSemaphore.acquire();
    }

    public synchronized void release() {
        if (countingSemaphore.availablePermits() != 1) {
            countingSemaphore.release();
        }
    }
}
