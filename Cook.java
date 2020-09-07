package cmsc433;

import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;

	/**
	 * You can feel free to modify this constructor.  It must
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
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(Simulation.flag) {
				Order order;
				
				synchronized(Simulation.orders) {
					order = Simulation.orders.poll();
				}
				
				if (order != null) {
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order.foodItems, order.orderNum));
					Thread[] foodThreads = new Thread[order.foodItems.size()];
					
					for (int i = 0; i < foodThreads.length; i++) {
						if (order.foodItems.get(i).name.equals("soda")) {
							foodThreads[i] = Simulation.machines[0].makeFood();
						} else if (order.foodItems.get(i).name.equals("wings")) {
							foodThreads[i] = Simulation.machines[1].makeFood();
						} else if (order.foodItems.get(i).name.equals("sub")) {
							foodThreads[i] = Simulation.machines[2].makeFood();
						} else if (order.foodItems.get(i).name.equals("pizza")) {
							foodThreads[i] = Simulation.machines[3].makeFood();
						}
						Simulation.logEvent(SimulationEvent.cookStartedFood(this, order.foodItems.get(i), order.orderNum));
						foodThreads[i].start();
					}
					
					for (int i = 0; i < foodThreads.length; i++) {
						foodThreads[i].join();
						Simulation.logEvent(SimulationEvent.cookFinishedFood(this, order.foodItems.get(i), order.orderNum));
					}
					
					synchronized(order) {
						Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, order.orderNum));
						order.notifyAll();
					}
				}
			}
			
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}
