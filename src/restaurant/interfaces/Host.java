package restaurant.interfaces;

import java.util.List;

import CMRestaurant.roles.CMWaiterRole;
import city.PersonAgent;
import city.roles.Role;


/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Host{

	public abstract void msgReleaveFromDuty(PersonAgent p);
	public abstract void msgReadyToWork(Waiter w);
	
	public abstract void msgIWantToEat(Customer c);
	
	public abstract void msgLeavingRestaurant(Customer c);
	
	//public abstract void msgTableIsFree(Waiter w, int msgerT);
	
	public abstract void msgCanIBreak(Waiter w);
	
	public abstract String getName();

	public abstract void msgDoneWorking(Waiter w);
	
	public abstract void goesToWork();
	public abstract void msgCloseRestaurant();
	public abstract void msgOpenRestaurant();
	
}
