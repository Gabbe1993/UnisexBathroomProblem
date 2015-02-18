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
    static Semaphore semBlock = new Semaphore(0);
    private boolean allowedToEnter = true;

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
            System.out.println(worker.info + " done at bathroom");
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
        //            try {
        //                if (worker instanceof Man) {
        //                    semMenAllowed.acquire();
        //                } else {
        //                    semMenAllowed.release();
        //                }
        //            } catch (InterruptedException ex) {
        //                Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
        //            }
        //        }
        if (semMenAllowed.availablePermits() == 1 && worker instanceof Man) {
            //System.out.println("ACCESS DENIED for: " + worker.info);
            return false;
        } else if (semMenAllowed.availablePermits() == 0 && worker instanceof Woman) {
            //System.out.println("ACCESS DENIED for: " + worker.info);
            return false;
        } 
        if(!allowedToEnter) {
            return false;
//        else if (semBlock.availablePermits() == 1) { // om det finns ledig sem (lås)
//            //System.out.println("ACCESS BLOCKED for: " + worker.info);
//            return false;
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
                allowedToEnter = true;
//                if (semBlock.availablePermits() < 1) {
//                    semBlock.release(); // sem++, dvs finns nu en ledig sem
//
//                    System.out.println("REALESE avaiblePermits == " + semBlock.availablePermits());
                    System.out.println("---- OPPOSITE GENDER MAY NOW ENTER BATHROOM ----");
//                }
            } 
//            else if (inBathroom > 0) {
//                semBlock.tryAcquire(); // sem --, dvs finns ingen ledig sem
//                System.out.println("ACCURIE avaiblePermits == " + semBlock.availablePermits());
                
            else if (worker instanceof Man && sem.women != 0) {
                semMenAllowed.release();
                allowedToEnter = false;
                System.out.println("--MEN DENIED--");

            } else if (worker instanceof Woman && sem.men != 0) {
                semMenAllowed.acquire();
                allowedToEnter = false;
                System.out.println("--WOMEN DENIED--");
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
