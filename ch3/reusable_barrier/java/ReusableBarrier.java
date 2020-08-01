import java.util.concurrent.Semaphore;

public class ReusableBarrier {

    class MyThread extends Thread {
        Semaphore barrier1;
        Semaphore barrier2;
        Semaphore mutex;
        int[] count;    // Number of threads that arrived to the barrier
        int nThreads;
        int nIterations;

        MyThread(Semaphore b1, Semaphore b2, Semaphore m, int[] count, int numOfThreads, int numIterations) {
            barrier1 = b1;
            barrier2 = b2;
            mutex = m;
            this.count = count;
            nThreads = numOfThreads;
            nIterations = numIterations;
        }

        public void run() {
            for (int i = 0; i < nIterations; i++) {
                // Phase 1
                acquire(mutex);
                count[0]++;
                if (count[0] == nThreads) {     // All arrived at gate 1
                    acquire(barrier2);          // Lock the second gate
                    barrier1.release();         // Open the first gate
                }
                mutex.release();
                acquire(barrier1);
                barrier1.release();

                System.out.println("Critical section of thread " + this.getName() + " at iteration " + (i + 1));

                // Phase 2
                acquire(mutex);
                count[0]--;
                if (count[0] == 0) {            // All arrived at gate 2
                    acquire(barrier1);          // Lock the first gate
                    barrier2.release();         // Open the second gate
                }
                mutex.release();
                acquire(barrier2);
                barrier2.release();
            }
        }

        private void acquire(Semaphore s) {
            try {
                s.acquire();
            }
            catch (Exception e) {
                System.out.println("Failed to acquire semaphore in thread " + this.getName());
            }
        }
    }

    public static void main(String[] args) {
        ReusableBarrier s = new ReusableBarrier();
        int numOfThreads = 7;
        int numOfIterations = 5;
        int[] countArrived = {0};
        Semaphore phase1 = new Semaphore(0);
        Semaphore phase2 = new Semaphore(1);    // Second turnstile is initially open
        Semaphore mutex = new Semaphore(1);
        MyThread[] threads = new MyThread[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            MyThread a = s.new MyThread(phase1, phase2, mutex, countArrived, numOfThreads, numOfIterations);
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
