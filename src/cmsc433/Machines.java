package cmsc433;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Machines are used to make different kinds of Food. Each Machine type makes
 * just one kind of Food. Each machine type has a count: the set of machines of
 * that type can make that many food items in parallel. If the machines are
 * asked to produce a food item beyond its count, the requester blocks. Each
 * food item takes at least item.cookTime10S seconds to produce. In this
 * simulation, use Thread.sleep(item.cookTime10S) to simulate the actual cooking
 * time.
 */
public class Machines {

	public enum MachineType {
		sodaMachines, fryers, grillPresses, ovens
	};

	// Converts Machines instances into strings based on MachineType.
	public String toString() {
		switch (machineType) {
			case sodaMachines:
				return "Soda Machines";
			case fryers:
				return "Fryers";
			case grillPresses:
				return "Grill Presses";
			case ovens:
				return "Ovens";
			default:
				return "INVALID MACHINE TYPE";
		}
	}

	public final MachineType machineType;
	public final Food machineFoodType;

	// YOUR CODE GOES HERE...
	//some variable to store count
	private Semaphore machineCanCookCapacity;

	/**
	 * The constructor takes at least the name of the machines, the Food item they
	 * make, and their count. You may extend it with other arguments, if you wish.
	 * Notice that the constructor currently does nothing with the count; you must
	 * add code to make use of this field (and do whatever initialization etc. you
	 * need).
	 */
	public Machines(MachineType machineType, Food foodIn, int countIn) {
		this.machineType = machineType;
		this.machineFoodType = foodIn;

		// YOUR CODE GOES HERE...
		this.machineCanCookCapacity = new Semaphore(countIn, true);


	}

	/**
	 * This method is called by a Cook in order to make the Machines' food item. You
	 * can extend this method however you like, e.g., you can have it take extra
	 * parameters or return something other than Object. You will need to implement
	 * some means to notify the calling Cook when the food item is finished.
	 */
	public Thread makeFood() throws InterruptedException {
		// YOUR CODE GOES HERE...
		
		//every time makeFood is called, it starts a new thread cooking some food.
		
		//maybe make each thread try and acquire a semaphore?
		//start all the threads, but whether each item will be cooking will depend on if
		//there is room in the machine to cook the desired food.
		Thread cookItemThread = new Thread(new CookAnItem(this.machineFoodType, this));
		
		
		cookItemThread.start();
		return cookItemThread;
	}

	// THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		//private CountDownLatch doneCookingItem;
		private Food foodToCook;
		private Machines machineToUse;
		
		private CookAnItem(Food foodBeingCooked, Machines machineBeingUsed) {
			//this.doneCookingItem = doneCooking;
			this.foodToCook = foodBeingCooked;
			this.machineToUse = machineBeingUsed;
		}
		
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				//throw new InterruptedException(); // REMOVE THIS
				//does this do what I want?
				//it should try to acquire the Semaphore for this Machine
				//and if it fails to, i.e., this machine has enough threads running on it
				//(full of items being cooked in it), then it should wait until another thread finishes
				//cooking their item
				machineCanCookCapacity.acquire();
				Simulation.logEvent(SimulationEvent.machinesCookingFood(this.machineToUse, this.foodToCook));
				
				//ccoking requires the thread to sleep a certain amount of time
				Thread.sleep(this.foodToCook.cookTime10S);
				
				Simulation.logEvent(SimulationEvent.machinesDoneFood(this.machineToUse, this.foodToCook));
				//release the semaphore since finished cooking the item.
				machineCanCookCapacity.release();
			} catch(InterruptedException e) { }
		}
	}
}
