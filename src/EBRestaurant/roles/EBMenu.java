package EBRestaurant.roles;

import java.util.HashMap;

public class EBMenu {
	HashMap<String,Float> hm=new HashMap<String,Float>();
	
	public EBMenu(){
		super();
		
		hm.put("Steak", (float) 15.99);
		hm.put("Chicken", (float) 10.99);
		hm.put("Salad", (float) 5.99);
		hm.put("Pizza", (float) 8.99);
	}
}
