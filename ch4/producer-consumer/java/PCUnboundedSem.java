package com.jrufus.conc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by rufus on 7/19/19.
 */
public class PCUnboundedSem {
    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();
        Semaphore mutex = new Semaphore(1);
        Semaphore available = new Semaphore(0);

        Producer producer = new Producer(deque, mutex, available);
        Consumer consumer = new Consumer(deque, mutex, available);

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(producer);
        executorService.submit(producer);
        executorService.submit(consumer);
        executorService.submit(producer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(producer);

        executorService.shutdown();
    }

}

class Producer implements Runnable {
    Deque<Integer> deque;
    Semaphore mutex, available;
    int count = 1;

    Producer(Deque<Integer> deque, Semaphore mutex, Semaphore available) {
        this.deque = deque;
        this.mutex = mutex;
        this.available = available;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500 * new Random().ints(1, 10).limit(1).findFirst().getAsInt());
            this.mutex.acquire();
            deque.add(count);
            count += 1;
            this.mutex.release();

            this.available.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    Deque<Integer> deque;
    Semaphore mutex, available;

    Consumer(Deque<Integer> deque, Semaphore mutex, Semaphore available) {
        this.deque = deque;
        this.mutex = mutex;
        this.available = available;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500 * new Random().ints(1, 10).limit(1).findFirst().getAsInt());
            this.available.acquire();
            this.mutex.acquire();
            System.out.println(deque.removeFirst());
            this.mutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
