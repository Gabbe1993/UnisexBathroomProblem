package unisexbathroomproblem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class Bathroom implements Runnable {

    public LinkedList<Worker> womanQueue;
    public LinkedList<Worker> manQueue;
    public static Semaphore sem = new Semaphore(1);
    private int workers = 10;

    public Bathroom() {
        for (int i = 0; i < workers; i++) {
            manQueue.add(new Man(this));
            womanQueue.add(new Woman(this));
        }
    }

    public void useBathroom(LinkedList<Worker> workers) throws InterruptedException {
        //TODO: fixa här
        System.out.println("Bathroom occupied...");
        sem.acquire();
        for (int i = 0; i < workers.size(); i++) {
            workers.get(i).useBathroom();
        }
        sem.release();
        System.out.println("Bathroom avaible");
    }

    private void handler() {

    }

    @Override
    public void run() {
        long manTime = 0, womanTime = 0;
        while (true) {
            //TODO: handle fairness here
            // Ide: summera totaltiden för varje kö. Räkna även med tidensedan de förta kom till kön?
            for (Worker manQueue1 : manQueue) {
                manTime += manQueue1.bathroomTime;
            }
            for (Worker womanQueue1 : womanQueue) {
                womanTime += womanQueue1.bathroomTime;
            }
            if (manTime < womanTime) {
                for (Worker manQueue1 : manQueue) {
                    manQueue1.useBathroom();
                }
            } else {
                for (Worker womanQueue1 : womanQueue) {
                    womanQueue1.useBathroom();
                }
            }
        }
    }

    void placeInQueue(Worker person) {
        if (person instanceof Man) {
            manQueue.add(person);
            System.out.println("Added MAN to manQueue");
        } else {
            womanQueue.add(person);
            System.out.println("Added WOMAN to womanQueue");
        }

    }
}
