package city.roles;

public abstract class Role 
{
	PersonAgent myPerson; 
	public boolean active = false;
	public void setPerson(PersonAgent a) {myPerson=a;} 
	public PersonAgent getPersonAgent() { return myPerson;} //so other agents or role 
	
	
	public Role(PersonAgent p)
	{
	        this.myPerson = p;
	}
	
	//players can send you Person messages.
	private void stateChanged()
	{
		this.active = true;
		myPerson.stateChanged();
	}
	public boolean isActive() {
	        return active;
	}
	public abstract boolean pickAndExecuteAnAction();
}