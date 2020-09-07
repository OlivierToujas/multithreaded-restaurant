package cmsc433;

import java.util.concurrent.Semaphore;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeS seconds to produce.
 */

public class Machine {
	
	// Types of machines used in Ratsie's.  Recall that enum types are
	// effectively "static" and "final", so each instance of Machine
	// will use the same MachineType.
	
	public enum MachineType { fountain, fryer, grillPress, oven };
	
	// Converts Machine instances into strings based on MachineType.
	
	public String toString() {
		switch (machineType) {
		case fountain: 		return "Fountain";
		case fryer:			return "Fryer";
		case grillPress:	return "Grill Press";
		case oven:			return "Oven";
		default:			return "INVALID MACHINE";
		}
	}
	
	public final MachineType machineType;
	public final Food machineFoodType;
	public final int capacity;
	
	//YOUR CODE GOES HERE...
	private final Semaphore machineSem;

	/**
	 * The constructor takes at least the type of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(MachineType machineType, Food food, int capacityIn) {
		this.machineType = machineType;
		this.machineFoodType = food;
		this.capacity = capacityIn;
		
		//YOUR CODE GOES HERE...
		this.machineSem = new Semaphore(capacityIn);
	}

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public Thread makeFood() throws InterruptedException {
		//YOUR CODE GOES HERE...
		CookAnItem c = new CookAnItem();
		Thread t = new Thread(c);
		return t;
	}
	
	public void startMachine() {
		Simulation.logEvent(SimulationEvent.machineStarting(this, machineFoodType, capacity));
	}
	
	public void stopMachine() {
		Simulation.logEvent(SimulationEvent.machineEnding(this));
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		public void run() {
			try {
				machineSem.acquire();
				Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
				Thread.sleep(machineFoodType.cookTimeS);
				Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
			} catch(InterruptedException e) { 
				System.out.println(e.getMessage());
			} finally {
				machineSem.release();
			}
			
		}
	}
}
