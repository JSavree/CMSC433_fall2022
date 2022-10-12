package cmsc433;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Order {
	private String customerName;
	private int customerOrderNum;
	private List<Food> customerOrder;
	private CountDownLatch customerLatch;
	
	public Order(String customerName, int orderNum, List<Food> order, CountDownLatch customerLatch) {
		this.customerName = customerName;
		this.customerOrderNum = orderNum;
		this.customerOrder = order;
		this.customerLatch = customerLatch;
	}
	
	public String getCustomerName() {
		return this.customerName;
	}
	
	public int getOrderNum() {
		return this.customerOrderNum;
	}
	
	public List<Food> getCustomerOrder() {
		return this.customerOrder;
	}
	
	public CountDownLatch getCustomerLatch() {
		return this.customerLatch;
	}
}
