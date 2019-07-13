package july2019;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Rendezvous {
	Semaphore a1Arrived = new Semaphore(1);
	Semaphore b1Arrived = new Semaphore(1);
	
	class A1 extends Thread {
		public void run(){
			try {
			System.out.println("Did a1");
			a1Arrived.release();
			
			Random r = new Random();
			Thread.sleep(r.nextInt(10000));

				b1Arrived.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Did a2");
			
		}
	}

	class B1 extends Thread {
		
		public void run() {
			try {
				System.out.println("Did b1");
				b1Arrived.release();
				Random r = new Random();
				Thread.sleep(r.nextInt(10000));
				a1Arrived.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Did b2");
			
		}

	}
	Thread t1 = new A1();
	Thread t2 = new B1();

	public static void main(String[] args) {
		
	Rendezvous r1 = new Rendezvous();
	r1.t1.start();
	r1.t2.start();
	}
}



