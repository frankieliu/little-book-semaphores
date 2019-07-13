package com.jrufus.conc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by rufus on 7/13/19.
 */
public class Barrier1 {
    public static void main(String[] args) {
        int n = 5;
        ExecutorService service = Executors.newFixedThreadPool(n);
        Barrier b = new Barrier(5);
        for(int i = 0; i < n; i++) {
            final int j = i;
            service.submit(() -> {
                b.exec(j);
            });
        }
        service.shutdown();

    }
}

class Barrier {
    Barrier(int n) {
        this.n = n;
    }

    Lock lock = new ReentrantLock();
    int count = 0;
    private int n;
    Semaphore barrier = new Semaphore(0);

    public void exec(int threadid) {
        lock.lock();
        try {
            count += 1;
        } finally {
            lock.unlock();
        }

        if(count == n) {
            barrier.release();
        }

        System.out.println("Barrier from thread : " + threadid);
        try {
            barrier.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        barrier.release();

        System.out.println("Critical section from thread : " + threadid);
    }
}

