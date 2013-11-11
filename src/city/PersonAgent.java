package city;

public class PersonAgent
{
	private List<Role> roles = new ArrayList<Role>();
	//hacked in upon creation
	private List<Restaurant> restaurants = new ArrayList<Restaurant>(); 
	
	
	        enum state { };
	        enum event { };
	private String name;
	        Time currentTime;
	        int hungerLevel = 0;
	public PersonAgent(String name)
	{
	        this.name = name;
	}
	public void addRole(Role r) {
	        roles.add(r);
	        r.setPerson(this);
	}
	public void setName(String name){
	        this.name = name;
	}
	        public String getName(){
	                return name;
	}
	        public boolean pickAndExecuteAnAction()
	{
	        try
	{
	//~NOTE~ only one role should be active at a time
	        for(Role r : roles)
	        {
	if( r.isActive() )
	{
	        r.PickAndExecuteAnAction();
	        return true;
	}
	}
	return false;
	}
	catch(CurrentModificationException e){ return false; }
	}
}