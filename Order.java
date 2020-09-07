package cmsc433;

import java.util.List;

public class Order {
	
	public final List<Food> foodItems;
	public final int orderNum;
	
	public Order (List<Food> foodItems, int orderNum) {
		this.foodItems = foodItems;
		this.orderNum = orderNum;
	}
}
