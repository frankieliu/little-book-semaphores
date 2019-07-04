package com.jrufus.conc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by rufus on 7/4/19.
 */
public class Rendezvous1 {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        executorService.submit(() -> {
            System.out.println("Executing a1");
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Executing a2");
        });

        executorService.submit(() -> {
            System.out.println("Executing b1");
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Executing b2");
        });

        executorService.shutdown();

    }

}

