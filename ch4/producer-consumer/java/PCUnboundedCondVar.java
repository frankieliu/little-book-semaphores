package com.jrufus.conc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by rufus on 7/19/19.
 */

public class PCUnboundedCondVar {
    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();
        Lock lock = new ReentrantLock();
        Condition isAvailable = lock.newCondition();
        Counter count = new Counter();

        ProducerC producer = new ProducerC(deque, lock, isAvailable, count);
        ConsumerC consumer = new ConsumerC(deque, lock, isAvailable, count);

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

class Counter {
    public int count;
}

class ProducerC implements Runnable {
    Deque<Integer> deque;
    Lock mutex;
    Condition available;
    int count = 1;
    Counter counter;

    ProducerC(Deque<Integer> deque, Lock mutex, Condition available, Counter counter) {
        this.deque = deque;
        this.mutex = mutex;
        this.available = available;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500 * new Random().ints(1, 10).limit(1).findFirst().getAsInt());
            this.mutex.lock();
            deque.add(count);
            counter.count += 1;
            count += 1;
            this.available.signal();
            this.mutex.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ConsumerC implements Runnable {
    Deque<Integer> deque;
    Lock mutex;
    Condition available;
    Counter counter;

    ConsumerC(Deque<Integer> deque, Lock mutex, Condition available, Counter counter) {
        this.deque = deque;
        this.mutex = mutex;
        this.available = available;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500 * new Random().ints(1, 10).limit(1).findFirst().getAsInt());
            this.mutex.lock();
            while(counter.count < 1) {
                this.available.await();
            }
            this.counter.count -= 1;
            System.out.println(deque.removeFirst());
            this.mutex.unlock();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
