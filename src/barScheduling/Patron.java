//M. M. Kuttel 2024 mkuttel@gmail.com
package barScheduling;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

//
public class Patron extends Thread {
	
	private Random random = new Random();// for variation in Patron behaviour

	private CountDownLatch startSignal; //all start at once, actually shared
	private Barman theBarman; //the Barman is actually shared though

	private int ID; //thread ID 
	private int lengthOfOrder;
	private long startTime, endTime; //for all the metrics
	
	public static FileWriter fileW;


	private DrinkOrder [] drinksOrder;
	
	Patron( int ID,  CountDownLatch startSignal, Barman aBarman) {
		this.ID=ID;
		this.startSignal=startSignal;
		this.theBarman=aBarman;
		this.lengthOfOrder=random.nextInt(5)+1;//between 1 and 5 drinks
		drinksOrder=new DrinkOrder[lengthOfOrder];
	}
	
	public  void writeToFile(String data) throws IOException {
	    synchronized (fileW) {
	    	fileW.write(data);
	    }
	}
	
	

	public void run() {
		try {
			//Do NOT change the block of code below - this is the arrival times
			startSignal.countDown(); //this patron is ready
			startSignal.await(); //wait till everyone is ready
	        int arrivalTime = random.nextInt(300)+ID*100;  // patrons arrive gradually later
	        sleep(arrivalTime);// Patrons arrive at staggered  times depending on ID 
			System.out.println("thirsty Patron "+ this.ID +" arrived");
			//END do not change


			long executionTime = 0;
			long totalOrder = 0;
			long firstInstance = 0;

	        //create drinks order
	        for(int i=0;i<lengthOfOrder;i++) {
	        	drinksOrder[i]=new DrinkOrder(this.ID);
	        	
	        }
			System.out.println("Patron "+ this.ID + " submitting order of " + lengthOfOrder +" drinks"); //output in standard format  - do not change this
	        startTime = System.currentTimeMillis();//started placing orders
			for(int i=0;i<lengthOfOrder;i++) {
				System.out.println("Order placed by " + drinksOrder[i].toString());
				theBarman.placeDrinkOrder(drinksOrder[i]);
			}
			for(int i=0;i<lengthOfOrder;i++) {
				drinksOrder[i].waitForOrder();
				//TODO
				//Assistance in calculating response time
				if(i == 0){
					firstInstance = System.currentTimeMillis();
				}
			}

			endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			long responseTime = firstInstance - startTime;

			// TODO
			// Calculating Waiting time
			int i;
			for( i = 0; i < drinksOrder.length; i++){
				executionTime = drinksOrder[i].getExecutionTime();
				totalOrder = totalOrder + executionTime;
			}
			long waitingTime = totalTime - totalOrder;


			writeToFile( String.format("%d,%d,%d,%d,%d\n",ID,arrivalTime,totalTime, waitingTime, responseTime));
			System.out.println("Patron "+ this.ID + " got order in " + totalTime);
			
			
		} catch (InterruptedException e1) {  //do nothing
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
}
}
	

