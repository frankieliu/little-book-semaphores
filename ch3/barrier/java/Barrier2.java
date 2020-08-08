import java.util.concurrent.Semaphore;

public class Barrier {

    class MyThread extends Thread {
        Semaphore barrier;
        Semaphore mutex;
        int[] count;
        int n;
        MyThread(Semaphore b, Semaphore m, int[] count, int numOfThreads) {
            barrier = b;
            mutex = m;
            this.count = count;
            n = numOfThreads;
        }
        public void run() {
            System.out.println("Statement #1 for thread " + this.getName());
            try {
                mutex.acquire();
            }
            catch (Exception e) {
                System.out.println("mutex.acquire exception " + e + " in thread " + this.getName());
            }
            count[0] = count[0] + 1;
            if (count[0] == n) {
                barrier.release();
            }
            mutex.release();
            System.out.println("mutex released by thread " + this.getName());
            try {
                barrier.acquire();
            }
            catch (Exception e) {
                System.out.println("barrier.acquire exception " + e + " in thread " + this.getName());
            }
            barrier.release();
            System.out.println("Statement #2 for thread " + this.getName());
        }
    }

    public static void main(String[] args) {
        Barrier s = new Barrier();
        int numOfThreads = 7;
        int[] count = {0};
        Semaphore allArrived = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);
        MyThread[] threads = new MyThread[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            MyThread a = s.new MyThread(allArrived, mutex, count, numOfThreads);
            threads[i] = a;
            a.start();
        }
        for (int i = 0; i < numOfThreads; i++) {
            try {
                threads[i].join();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
