package restaurant.test.mock;

import restaurant.WaiterAgent.Menu;
import restaurant.gui.CustomerGui;
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

	@Override
	public void msgHereIsCheck(double check) {
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Total = "+ check));

		if((this.name.toLowerCase().contains("mahdi") || this.name.toLowerCase().contains("ditch")) && !goToATM){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			cashier.msgPayment(this, 0);

		}else if (this.name.toLowerCase().contains("rich")){
			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
			cashier.msgPayment(this, Math.ceil(check));

		}else{
			//test the normative scenario
			cashier.msgPayment(this, check);
		}
	}

	@Override
	public void msgChange(double cashBack) {
		log.add(new LoggedEvent("Received msgChange from cashier. Change = "+ cashBack));
	}

	@Override
	public void msgPayMeLater() {
		log.add(new LoggedEvent("Received msgPayMeLater from cashier. Go to ATM."));
		goToATM = true;
	}

	@Override
	public void msgFollowMeToTable(Waiter w, Menu m) {
		log.add(new LoggedEvent("Received msgFollowMeToTable from waiter."));
	}

	@Override
	public void msgWhatWouldYouLike() {		
		log.add(new LoggedEvent("Received msgWhatWouldYouLike from waiter."));
	}

	@Override
	public void msgOutOfOrder(String c) {		
		log.add(new LoggedEvent("Received msgOutOfOrder from waiter."));
	}

	@Override
	public void msgHereIsYourFood() {
		log.add(new LoggedEvent("Received msgHereIsYourFood from waiter."));
	}
	
	public void msgWaitForOpenTable() {
	}
	
	public void msgTableIsReady() {
	}

	@Override
	public CustomerGui getGui() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
