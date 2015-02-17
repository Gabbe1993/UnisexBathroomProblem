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

    static Semaphore sem = new Semaphore(1000000000);

    public Bathroom(int workers) {
        for (int i = 0; i < workers; i++) {
            Thread manT = new Thread(new Man(this));
            manT.start();
            Thread womanT = new Thread(new Woman(this));
            womanT.start();
        }
        calculateNext();
    }

    void useBathroom(Worker worker) {
        try {
            sem.acquire();
            System.out.println("Bathroom occupied ...");

            int longestBt = 0, tempBt = 0;

            tempBt = worker.bathroomTime;

            if (tempBt > longestBt) {
                longestBt = tempBt;
            }
            Thread.sleep(tempBt);

            sem.release();
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Bathroom avaible");

            calculateNext();

        }

    }

    private void calculateNext() {

        if (manQueue.getFirst().arrivedTime < womanQueue.getFirst().arrivedTime) {
            manQueue.getFirst().useBathroom();
        } else {
            womanQueue.getFirst().useBathroom();
        }

        //      long manArrived = 0, womanArrived = 0, tempArrived = 0;
//        for (int i = 0; i < manQueue.size(); i++) {
//            tempArrived = manQueue.get(i).arrivedTime;
//            if (tempArrived < manArrived) {
//                manArrived = tempArrived;
//            }
//        }
//        tempArrived = 0;
//        for (int i = 0; i < womanQueue.size(); i++) {
//            tempArrived = womanQueue.get(i).arrivedTime;
//            if (tempArrived < womanArrived) {
//                womanArrived = tempArrived;
//            }
//        }
//        if (womanArrived < manArrived) {
//            for (Worker womanQueue1 : womanQueue) {
//                womanQueue1.useBathroom();
//            }
//            useBathroom(womanQueue);
//        } else {
//            for (Worker manQueue1 : manQueue) {
//                manQueue1.useBathroom();
//            }
//            useBathroom(manQueue);
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
