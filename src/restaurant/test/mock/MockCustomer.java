package restaurant.test.mock;

import CMRestaurant.gui.CMCustomerGui;
import CMRestaurant.roles.*;
import restaurant.interfaces.Waiter.Menu;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockCustomer(String name) {
		super(name);

	}

	public void msgHereIsCheck(double check) {
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Total = "+ check));

		if((this.name.toLowerCase().contains("mahdi") || this.name.toLowerCase().contains("ditch")) && !goToATM){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			((CMCashierRole) cashier).msgPayment(this, 0);

		}else if (this.name.toLowerCase().contains("rich")){
			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
			((CMCashierRole) cashier).msgPayment(this, Math.ceil(check));

		}else{
			//test the normative scenario
			((CMCashierRole) cashier).msgPayment(this, check);
		}
	}

	
	public void msgChange(double cashBack) {
		log.add(new LoggedEvent("Received msgChange from cashier. Change = "+ cashBack));
	}

	
	public void msgPayMeLater() {
		log.add(new LoggedEvent("Received msgPayMeLater from cashier. Go to ATM."));
		goToATM = true;
	}

	
	public void msgFollowMeToTable(Waiter w, Menu m) {
		log.add(new LoggedEvent("Received msgFollowMeToTable from waiter."));
	}

	
	public void msgWhatWouldYouLike() {		
		log.add(new LoggedEvent("Received msgWhatWouldYouLike from waiter."));
	}

	
	public void msgOutOfOrder(String c) {		
		log.add(new LoggedEvent("Received msgOutOfOrder from waiter."));
	}

	
	public void msgHereIsYourFood() {
		log.add(new LoggedEvent("Received msgHereIsYourFood from waiter."));
	}
	
	public void msgWaitForOpenTable() {
	}
	
	public void msgTableIsReady() {
	}

	@Override
	public void gotHungry() {
		// TODO Auto-generated method stub
		
	}
	
}
