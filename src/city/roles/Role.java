package city.roles;

import city.PersonAgent;

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
	public void stateChanged()
	{
		this.active = true;
		myPerson.stateChanged();
	}
	public boolean isActive() {
	        return active;
	}
	
    /**
     * The simulated action code
     */
    protected void Do(String msg) {
        print(msg);
    }

    /**
     * Print message
     */
    protected void print(String msg) {
        print(msg);
    }
	
	public abstract boolean pickAndExecuteAnAction();
}