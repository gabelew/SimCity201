package EBRestaurant.roles;

import java.util.HashMap;

public class EBMenu {
	HashMap<String,Float> hm=new HashMap<String,Float>();
	
	public EBMenu(){
		super();
		
		hm.put("steak", (float) 15.99);
		hm.put("chicken", (float) 10.99);
		hm.put("salad", (float) 5.99);
		hm.put("cookie", (float) 8.99);
	}
}
