package unisexbathroomproblem;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class Bathroom {

    LinkedList<Worker> womanQueue = new LinkedList<>();
    LinkedList<Worker> manQueue = new LinkedList<>();

    private ReentrantLock manQueueLock = new ReentrantLock();
    private ReentrantLock womanQueueLock = new ReentrantLock();
    private ReentrantLock semLock = new ReentrantLock();

    boolean activeMan;

    static final int DEFAULT_MAX_LIMIT = 1000000000;
    static GenderSemaphore sem = new GenderSemaphore(DEFAULT_MAX_LIMIT);

    public Bathroom(int workers) {
        for (int i = 0; i < workers; i++) {
            Thread manT = new Thread(new Man(this));
            manT.start();
            Thread womanT = new Thread(new Woman(this));
            womanT.start();
        }
        while (true) {
            try {
                Thread.sleep(3000);
                calculateNext();
            } catch (InterruptedException ex) {
                Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //TODO: se till så att andra könet får gå in när alla lämnat
    //hämta alla semaphorerer, varje har enskild
    void useBathroom(Worker worker) {
        try {
            semLock.lock();
            sem.acquire(worker);

            if (worker instanceof Man) {
                activeMan = true;
            } else {
                activeMan = false;
            }

            Thread.sleep(worker.bathroomTime);

            sem.release(worker);
            semLock.unlock();

            // calculateNext();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    //FIX FAIR CALCULATION fulaste metoden EU dno låååssss??,,mmmmmmmmmmmmmmmm
    private void calculateNext() {
        if (sem.women == 0 && sem.men == 0) {
            useBathroom(manQueue.getFirst());
            manQueue.removeFirst();
        } else if (sem.women == 0 && sem.men != 0) {
            useBathroom(manQueue.getFirst());
            manQueue.removeFirst();
        } else if (sem.women != 0 && sem.men == 0) {
            useBathroom(womanQueue.getFirst());
            womanQueue.removeFirst();
        } else if (sem.women != 0 && sem.men != 0 && activeMan) {
            useBathroom(womanQueue.getFirst());
            womanQueue.removeFirst();
        } else if (sem.women != 0 && sem.men != 0 && !activeMan) {
            useBathroom(manQueue.getFirst());
            manQueue.removeFirst();
        }
    }

    void placeInQueue(Worker person) {
        if (person instanceof Man) {
            manQueueLock.lock();
            manQueue.add(person);
            manQueueLock.unlock();
            System.out.println("Added MAN to manQueue");
        } else {
            womanQueueLock.lock();
            womanQueue.add(person);
            womanQueueLock.unlock();
            System.out.println("Added WOMAN to womanQueue");
        }
    }
}
