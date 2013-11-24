package city.interfaces;

import java.util.Map;

public interface AtHome {
	
	public abstract void ImHungry();

	public abstract void restockFridge(Map<String,Integer> orderList);
	
	public abstract void ApplianceFixed(String app, double price);
	
	public abstract void msgGoLeaveHome();
	
}
