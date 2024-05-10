package barScheduling;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/*
 Barman Thread class.
 */

public class Barman extends Thread {

	AtomicInteger increment;
	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;

	ScheduledExecutorService checker = Executors.newScheduledThreadPool(1);


	
	Barman(CountDownLatch startSignal, int schedAlg, AtomicInteger increment) {
		if (schedAlg==0)
			this.orderQueue = new LinkedBlockingQueue<>();
		//FIX below
		// Uses priority Block Queue
		else this.orderQueue = new PriorityBlockingQueue<>(); //this creates a priority
		this.increment = increment;
	    this.startSignal=startSignal;
	}
	
	
	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException {
        orderQueue.put(order);
    }
	
	
	public void run() {
		try {
			DrinkOrder nextOrder;
			
			startSignal.countDown(); //barman ready
			startSignal.await(); //check latch - don't start until told to do so
			checker .scheduleAtFixedRate(new Background(increment),0,10,TimeUnit.SECONDS);

			while(true) {
				nextOrder=orderQueue.take();
				System.out.println("---Barman preparing order for patron "+ nextOrder.toString());
				sleep(nextOrder.getExecutionTime()); //processing order
				System.out.println("---Barman has made order for patron "+ nextOrder.toString());

				nextOrder.orderDone();
			}
				
		} catch (InterruptedException e1) {
			System.out.println("---Barman is packing up ");
			checker .shutdown();
		}
	}
}


