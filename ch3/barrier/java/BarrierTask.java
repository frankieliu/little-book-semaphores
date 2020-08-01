package concurrency.littlesemaphores.chp3;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BarrierTask implements Runnable {

  private CyclicBarrier barrier;

  public BarrierTask(CyclicBarrier barrier){
    this.barrier = barrier;
  }


  @Override
  public void run() {

    try {
      System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
      barrier.await();
      System.out.println(Thread.currentThread().getName() + " has crossed the barrier");

    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (BrokenBarrierException e) {
      e.printStackTrace();
    }

  }

}

class BarrierTaskRunner {

  public static void main(String[] args) {

    final CyclicBarrier cb = new CyclicBarrier(3, new Runnable() {
      @Override
      public void run() {
        System.out.println("All parties have arrived at the barrier, lets continue execution.");
      }
    });

    Thread t1 = new Thread(new BarrierTask(cb),"Thread 1");
    Thread t2 = new Thread(new BarrierTask(cb),"Thread 2");
    Thread t3 = new Thread(new BarrierTask(cb),"Thread 3");

    t1.start();
    t2.start();
    t3.start();

  }
}
