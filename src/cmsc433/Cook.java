package cmsc433;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Customer and process them.
 */
public class Cook implements Runnable {
	private final String name;

	/**
	 * You can feel free modify this constructor. It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful.
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows. The cook tries to retrieve
	 * orders placed by Customers. For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine type, by calling makeFood(). Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified. The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {
				// YOUR CODE GOES HERE..
				//use a CyclicBarrier to wait or just use latches.
				Order customerOrder;
				System.out.println("hello?, cook " + this.name + " here");
				//here Cook processes the order and removes it.
				synchronized (Simulation.orderQueue) {
					while (Simulation.orderQueue.isEmpty()) {
						Simulation.orderQueue.wait();
					}
					
					customerOrder = Simulation.orderQueue.remove();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customerOrder.getCustomerOrder(),
							customerOrder.getOrderNum()));
				}

				//tracks the items being cooked
				ArrayList<Thread> itemsBeingCooked = new ArrayList<Thread>();
				
				Machines machineUsed;
				for (Food orderItem : customerOrder.getCustomerOrder()) {
					//try and see if the Machine has room to cook food
					//Simulation.machinesMap.get(orderItem).machineCanCookCapacity.acquire();
					Simulation.logEvent(SimulationEvent.cookStartedFood(this, orderItem, customerOrder.getOrderNum()));	
					
					synchronized (Simulation.machinesMap) {
						machineUsed = Simulation.machinesMap.get(orderItem);
					}
					
					if (machineUsed == null) {
						System.err.println("machine does not exist for " + orderItem);
					} else {
						//start cooking item with the machine
						Thread itemCooked = machineUsed.makeFood();
						itemsBeingCooked.add(itemCooked);
					}
						
				}
				
				//wait for each item in the order to be done cooking
				for (int i = 0; i < customerOrder.getCustomerOrder().size(); i++) {
					itemsBeingCooked.get(i).join();
					Simulation.logEvent(SimulationEvent.cookFinishedFood(this, customerOrder.getCustomerOrder().get(i),
							customerOrder.getOrderNum()));
				}
				
				
				
				//this should be last thing done in the loop.
				//when finished cooking, inform customers, i.e., deliver their order
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, customerOrder.getOrderNum()));
				customerOrder.getCustomerLatch().countDown();

				//throw new InterruptedException(); // REMOVE THIS
			}
		} catch (InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}
