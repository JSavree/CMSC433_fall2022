package cmsc433;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order. When running, an
 * customer attempts to enter the Ratsie's (only successful if the
 * Ratsie's has a free table), place its order, and then leave the
 * Ratsie's when the order is complete.
 */
public class Customer implements Runnable {
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;

	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor. It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to
	 * enter the Ratsie's (only successful when the Ratsie's has a
	 * free table), place its order, and then leave the Ratsie's
	 * when the order is complete.
	 */
	public void run() {
		// YOUR CODE GOES HERE...
		//See if enough tables to enter Ratsie's
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		try {
			Simulation.numberOfTablesAvailable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Entered Ratsie's
		Simulation.logEvent(SimulationEvent.customerEnteredRatsies(this));
		
		//start countdown latch
	    CountDownLatch waitForOrder = new CountDownLatch(1);
		
	    synchronized (Simulation.orderQueue) {
	    	//place order
	    	Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
	    	Order thisCustomerOrder = new Order(this.name, this.orderNum, this.order, waitForOrder);
	    	Simulation.orderQueue.push(thisCustomerOrder);
	    	
	    	//let Cook know a order has been placed.
	    	Simulation.orderQueue.notifyAll();
	    }
	    
    	
    	//count down latch to wait for order to be finished by Cook
    	try {
			waitForOrder.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//after await release, customer should have received its order.
    	Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
    	
    	//Cook should be the one to remove the Customer's order after finishing processing it.
    	//Simulation.orderQueue.remove(thisCustomerOrder);
	    
	    //leaving Ratsie's
	    Simulation.logEvent(SimulationEvent.customerLeavingRatsies(this));
	    Simulation.numberOfTablesAvailable.release();
	}
}
