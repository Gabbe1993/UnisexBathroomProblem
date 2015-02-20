package src;

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

    int inBathroom = 0;
    static final int DEFAULT_MAX_LIMIT = 1000000000;

    // Counting semaphores
    static GenderSemaphore semGender = new GenderSemaphore(DEFAULT_MAX_LIMIT);
    static Semaphore semInBr = new Semaphore(DEFAULT_MAX_LIMIT);

    // Binary semaphores
    static Semaphore semMenLock = new Semaphore(1);
    static Semaphore semBrLock = new Semaphore(1);

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
    void enterBathroom(Worker worker) {
        try {
            //inBathroom++;
            semInBr.acquire();

            System.out.println(worker.info + " using bathroom for: " + worker.bathroomTime / 1000 + " sek");
            System.out.println("Workers in bathroom = " + (DEFAULT_MAX_LIMIT - semInBr.availablePermits()));
            Thread.sleep(worker.bathroomTime);

            //inBathroom--;
            semInBr.release();
            System.out.println(worker.info + " DONE at bathroom");
            //System.out.println("Workers in bathroom = " + inBathroom);
            System.out.println("Workers in bathroom = " + (DEFAULT_MAX_LIMIT - semInBr.availablePermits()));

            leaveBathroom(worker, (DEFAULT_MAX_LIMIT - semInBr.availablePermits()));
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
        if (isUnlocked()) {
            lockBr();
            unlockForGender(worker);
            return true;
        } else if (menAllowed() && worker instanceof Man) {
            return true;
        } else if (!menAllowed() && worker instanceof Woman) {
            return true;
        }

        return false;

    }

    /**
     * Calculates whether the access to the bathroom should be changed when a
     * person in leaves. The allowed gender is changed to: 1) Male allowed: if
     * there is waiting females that has acquired the semaphore and the last
     * leaver 2) Female allowed: if there is waiting males that has acquired the
     * semaphore and the last leaver is female
     *
     * @param worker the worker to leave the bathroom
     */
    private void leaveBathroom(Worker worker, int inBathroom) {
        if (inBathroom <= 0) {
            System.out.println("Waiting men = " + semGender.men + " Waiting women = " + semGender.women);
            unlockBr();

        } else if (isUnlocked()) {
            lockBr();
        }
        if (worker instanceof Man && semGender.women != 0) {
            lockForGender(worker);
        } else if (worker instanceof Woman && semGender.men != 0) {
            lockForGender(worker);
        }
    }

    private boolean menAllowed() {
        if (semMenLock.availablePermits() == 1) {
            System.out.println("MEN ALLOWED");
            return true;
        } else {
            System.out.println("MEN NOT ALLOWED");
            return false;
        }
    }

    private boolean isUnlocked() {
        if (semBrLock.availablePermits() == 1) {
            System.out.println("BATHROOM IS UNLOCKED");
            return true;
        } else {
            System.out.println("BATHROOM IS LOCKED");
            return false;
        }
    }

    private void unlockForGender(Worker worker) {
        if (worker instanceof Man) {
            System.out.println("------UNLOCKING FOR MEN-------");
            if (semMenLock.availablePermits() == 0) {
                semMenLock.release();
            }
        } else {
            System.out.println("------UNLOCKING FOR WOMEN------");
            semMenLock.tryAcquire();
        }
    }

    private void lockForGender(Worker worker) {
        if (worker instanceof Man) {
            semMenLock.tryAcquire();
            System.out.println("LOCKING FOR MEN");
        } else {
            System.out.println("LOCKING FOR WOMEN");
            semMenLock.release();
        }
    }

    private void lockBr() {
        System.out.println("------LOCKING BATHROOM------");
        semBrLock.tryAcquire();
    }

    private void unlockBr() {
        System.out.println("------UNLOCKING BATHROOM------");
        semBrLock.release();
    }
}
