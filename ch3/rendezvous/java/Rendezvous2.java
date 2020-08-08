import java.util.concurrent.Semaphore;

public class Rendezvous {

    class ThreadA extends Thread {
        Semaphore semA;
        Semaphore semB;

        ThreadA(Semaphore a, Semaphore b) {
            semA = a;
            semB = b;
        }

        public void run() {
            System.out.println("Statement a1");
            semA.release();
            try {
                semB.acquire();
            }
            catch (Exception e) {
                System.out.println("exception " + e + " in Thread A");
            }
            System.out.println("Statement a2");
        }

    }

    class ThreadB extends Thread {
        Semaphore semA;
        Semaphore semB;

        ThreadB(Semaphore a, Semaphore b) {
            semA = a;
            semB = b;
        }

        public void run() {
            System.out.println("Statement b1");
            semB.release();
            try {
                semA.acquire();
            }
            catch (Exception e) {
                System.out.println("exception " + e + " in Thread B");
            }
            System.out.println("Statement b2");
        }

    }

    public static void main(String[] args) {
        Rendezvous s = new Rendezvous();
        Semaphore aArrived = new Semaphore(0);
        Semaphore bArrived = new Semaphore(0);
        ThreadA a = s.new ThreadA(aArrived, bArrived);
        ThreadB b = s.new ThreadB(aArrived, bArrived);
        a.start();
        b.start();
        try {
            a.join();
            b.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
