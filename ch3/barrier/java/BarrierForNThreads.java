import java.util.Random;
import java.util.concurrent.Semaphore;

public class BarrierForNThreads {
	
	Semaphore sem = new Semaphore(0);
	Random rand = new Random();
	private final int total = 3;
	static int count=0;
	Object lock = new Object();
	
	public static void main(String[] args) {
		BarrierForNThreads obj = new BarrierForNThreads();
		
		for(int i=0; i<10; i++) {
			count = 0;
			Worker threadA = obj.new Worker("A");
			Worker threadB = obj.new Worker("B");
			Worker threadC = obj.new Worker("C");
			
			threadA.start();
			threadB.start();
			threadC.start();
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("--------------");
		}

		
	}
	
	class Worker extends Thread {

		String content = null;
		public Worker(String content) {
			this.content = content;
		}
		
		public void run() {
			System.out.println(content+"1");
			
			synchronized(lock) {
				count++;
			}
			
			if(count == total) sem.release();
			
			long sleepTime = rand.nextInt(1000);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				sem.acquire();
				sem.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(content+"2");
		}
	}
	
	
}
