package cmsc433;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the Ratsie's (only successful if the
 * Ratsie's has a free table), place its order, and then leave the
 * Ratsie's when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final Order order;
	    
	
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = new Order(order, ++runningCounter);
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
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		
		try {
			Simulation.tableSem.acquire();
			Simulation.logEvent(SimulationEvent.customerEnteredRatsies(this));
			
			synchronized(Simulation.orders) {
				Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order.foodItems, this.order.orderNum));
				Simulation.orders.add(this.order);
			}
			synchronized(this.order) {
				this.order.wait();
				Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order.foodItems, this.order.orderNum));
			}
			
			
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		} finally {
			Simulation.logEvent(SimulationEvent.customerLeavingRatsies(this));
			Simulation.tableSem.release();
		}
		
	}
}
