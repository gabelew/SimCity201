package city.roles;

import bank.gui.BankCustomerGui;
import agent.StringUtil;
import city.PersonAgent;
import city.gui.Gui;

public abstract class Role 
{
	public PersonAgent myPerson; 
	public boolean active = false;
	public PersonAgent getPersonAgent() { return myPerson;} //so other agents or role 
	
	

	public Role()
	{
	}
	public Role(PersonAgent p)
	{
	        this.myPerson = p;
	}

	public String getName() {
		if(myPerson != null){
			return myPerson.getName();
		}else{
			return "Empty Role";
		}
	}
	public PersonAgent getPerson(){
		return myPerson;
	}
	public void setPerson(PersonAgent p){
		this.myPerson = p;
	}

	//players can send you Person messages.
	public void stateChanged()
	{
		if(this.myPerson != null){
			if(this.myPerson.getStateChangePermits()==0){
				this.myPerson.stateChanged();
			}
		}
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
    public void print(String msg) {
        print(msg, null);
    }

    /**
     * Print message with exception stack trace
     */
    protected void print(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        if(myPerson != null){
        	sb.append(myPerson.getName());
    	}else{
            sb.append("emptyRole: ");
        }
        sb.append(": ");
        sb.append(msg);
        sb.append("\n");
        if (e != null) {
            sb.append(StringUtil.stackTraceString(e));
        }
        System.out.print(sb.toString());
    }
	
	public abstract boolean pickAndExecuteAnAction();

	public abstract void setGui(Gui g);

	public abstract Gui getGui();
}