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

public class PCBoundedCondVar {
    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();
        Lock lock = new ReentrantLock();
        Condition notEmpty = lock.newCondition();
        Condition notFull = lock.newCondition();
        Counter1 count = new Counter1();
        int max = 2;

        ProducerD producer = new ProducerD(deque, lock, notEmpty, notFull, count, max);
        ConsumerD consumer = new ConsumerD(deque, lock, notEmpty, notFull, count, max);

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(producer);
        executorService.submit(producer);
        executorService.submit(producer);
        executorService.submit(producer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(consumer);
        executorService.submit(producer);
        executorService.submit(producer);

        executorService.shutdown();
    }

}

class Counter1 {
    public int count;
}

class ProducerD implements Runnable {
    Deque<Integer> deque;
    Lock mutex;
    Condition notEmpty;
    Condition notFull;
    int count = 1;
    Counter1 counter;
    int max;

    ProducerD(Deque<Integer> deque, Lock mutex, Condition available, Condition notFull, Counter1 counter, int max) {
        this.deque = deque;
        this.mutex = mutex;
        this.notEmpty = available;
        this.counter = counter;
        this.max = max;
        this.notFull = notFull;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500 * new Random().ints(1, 10).limit(1).findFirst().getAsInt());
            this.mutex.lock();
            while(this.counter.count == max) {
                this.notFull.await();
            }
            deque.add(count);
            System.out.println("Producer adding: " +  count);
            counter.count += 1;
            count += 1;
            this.notEmpty.signal();
            this.mutex.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ConsumerD implements Runnable {
    Deque<Integer> deque;
    Lock mutex;
    Condition notEmpty;
    Condition notFull;
    Counter1 counter;
    int max;

    ConsumerD(Deque<Integer> deque, Lock mutex, Condition available, Condition notFull, Counter1 counter, int max) {
        this.deque = deque;
        this.mutex = mutex;
        this.notEmpty = available;
        this.notFull = notFull;
        this.counter = counter;
        this.max = max;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500 * new Random().ints(1, 10).limit(1).findFirst().getAsInt());
            this.mutex.lock();
            while(counter.count < 1) {
                this.notEmpty.await();
            }
            this.counter.count -= 1;
            System.out.println("Consumer : " + deque.removeFirst());
            this.notFull.signal();
            this.mutex.unlock();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
