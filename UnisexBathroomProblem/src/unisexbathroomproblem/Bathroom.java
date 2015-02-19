package unisexbathroomproblem;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The unisex bathroom problem solution: Workers tries to acquire a semaphore
 * when they need to go to bathroom, if granted they will go to the bathroom. If
 * not they will wait until they are granted. Only one sex is granted at a time,
 * and if another sex is in queue the bathroom will be closes and opened for the
 * opposite sex.
 *
 * @author Gabriel
 */
public class Bathroom {

    Worker lastWorker;
    int inBathroom = 0;
    static final int DEFAULT_MAX_LIMIT = 1000000000;
    static GenderSemaphore sem = new GenderSemaphore(DEFAULT_MAX_LIMIT);
    static Semaphore semMenAllowed = new Semaphore(1);
    static Semaphore semLock = new Semaphore(1);

    public Bathroom(int workers) {
        for (int i = 0; i < workers; i++) {
            Thread manT = new Thread(new Man(this, i));
            manT.start();
            Thread womanT = new Thread(new Woman(this, i));
            womanT.start();
        }
    }

    /**
     * The unisex bathroom that keeps track of the amount of workers currently
     * in the bathroom.
     *
     * @param worker the worker that enters the bathroom
     */
    void useBathroom(Worker worker) {
        try {
            //semLock.acquire();
            inBathroom++;

            System.out.println(worker.info + " using bathroom for: " + worker.bathroomTime / 1000 + " sek");
            System.out.println("Workers in bathroom = " + inBathroom);
            Thread.sleep(worker.bathroomTime);

            inBathroom--;
            //semLock.release();
            System.out.println(worker.info + " DONE at bathroom");
            System.out.println("Workers in bathroom = " + inBathroom);

            calculateAccess(worker, inBathroom);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks whether the worker is allowed to access the bathroom. The worker
     * is allowed if: 1) The bathroom is empty 2) The worker's gender is
     * currently in the bathroom and his/her gender has access
     *
     * @param worker worker requesting access
     * @return true if allowed to enter bathroom
     */
    public boolean allowed(Worker worker) {
        if (semMenAllowed.availablePermits() == 1 && worker instanceof Man) {
            //System.out.println("ACCESS DENIED for: " + worker.info);
            return false;

        } else if (semMenAllowed.availablePermits() == 0 && worker instanceof Woman) {
            //System.out.println("ACCESS DENIED for: " + worker.info);
            return false;

        } else if (isSemLocked()) {
            //System.out.println("ACCESS BLOCKED for: " + worker.info);
            return false;
        }
        
        return true;

    }

    /**
     * Calculates whether the access to the bathroom should be changed when the
     * last person in the bathroom leaves (bathroom becomes empty). The allowed
     * gender is changed to: 1) Male allowed: if there is waiting females that
     * has acquired the semaphore and the last leaver is male 2) Female allowed:
     * if there is waiting males that has acquired the semaphore and the last
     * leaver is female
     *
     * @param worker the last worker to leave the bathroom
     */
    private void calculateAccess(Worker worker, int inBathroom) {
        System.out.println("sem.men = " + sem.men + " sem.women = " + sem.women);
        try {
            if (inBathroom <= 0) {
                unlockSem();

            } else if (inBathroom > 0) {
                lockSem();

            }
            if (worker instanceof Man && sem.women != 0) {
                semMenAllowed.release();
                System.out.println("--MEN DENIED--");

            } else if (worker instanceof Woman && sem.men != 0) {
                semMenAllowed.acquire();
                System.out.println("--WOMEN DENIED--");
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Tries to lock the semaphore. This is the same as if(semaphore == 0)
     * semaphore++. The lock is locked when the semaphore is 1.
     */
    private void lockSem() {
        if (semLock.availablePermits() == 1) {
            try {
                semLock.acquire();
                System.out.println("semLock: semLock.acquire();");
                System.out.println("semLock: availablePermits (should be 0) = " + semLock.availablePermits());
            } catch (InterruptedException ex) {
                Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Tries to unlock the semaphore. It is the same as if(semaphore == 1)
     * semaphore--. The lock is unlocked when the semaphore is 0.
     */
    private void unlockSem() {
        if (semLock.availablePermits() == 0) {
            semLock.release();
            System.out.println("unlockSem: semLock.release()");
            System.out.println("unlockSem: availablePermits (should be 1) = " + semLock.availablePermits());
        }
    }

    private boolean isSemLocked() {
        if (semLock.availablePermits() == 0) {
            System.out.println("isSemLocked: LOCKED");
            return true;
        } else {
            System.out.println("isSemLocked: UNLOCKED");
            return false;
        }
    }

}
