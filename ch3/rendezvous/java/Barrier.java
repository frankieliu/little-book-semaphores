package Concurrency;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Multiplex{
//Puzzle: Generalize the previous solution so that it allows multiple threads to run in the critical section at the same time,
// but it enforces an upper limit on the number of concurrent threads. In other words, no more than n threads can run in the
// critical section at the same time.

    static Semaphore barrier;
    static Lock mutex;
    static int count;
    static Random rand;

    static class Worker extends Thread{
        String name;
        public Worker(String name){
            this.name = name;
        }
        public void run() {
            System.out.println(name+"1");
            mutex.lock();
            count++;
            mutex.unlock();
            System.out.println(count);
            if(count == 3) barrier.release();
            long sleepTime = rand.nextInt(1000);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                barrier.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            barrier.release();
            System.out.println(name+"2");
        }
    }

    public static void main(String[] args) {
        mutex = new ReentrantLock(true);
        rand = new Random();
        for(int i = 0; i < 10; i ++){
            count = 0;
            barrier = new Semaphore(0);
            Thread a = new Worker("A");
            Thread b = new Worker("B");
            Thread c = new Worker("C");
            a.start();
            b.start();
            c.start();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("--------------");
        }
    }
}