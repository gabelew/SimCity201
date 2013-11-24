package city.test.mock;

import java.util.Map;

import city.interfaces.AtHome;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.Mock;

public class MockAtHomeRole extends Mock implements AtHome{

	public EventLog log = new EventLog();
	
	public MockAtHomeRole(String name) {
		super(name);
	}

	@Override
	public void ImHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restockFridge(Map<String, Integer> orderList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ApplianceFixed(String app, double price) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoLeaveHome() {
		// TODO Auto-generated method stub
		
	}

}
