package city.roles;

import agent.StringUtil;
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
        print(msg, null);
    }

    /**
     * Print message
     */
    protected void print(String msg) {
        print(msg, null);
    }

    /**
     * Print message with exception stack trace
     */
    protected void print(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(myPerson.getName());
        sb.append(": ");
        sb.append(msg);
        sb.append("\n");
        if (e != null) {
            sb.append(StringUtil.stackTraceString(e));
        }
        System.out.print(sb.toString());
    }
	
	public abstract boolean pickAndExecuteAnAction();
}