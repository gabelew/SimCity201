package city.interfaces;

import city.PersonAgent;

public interface RepairMan 
{
	public abstract void fixAppliance(PersonAgent p, String app);
	
	public abstract void HereIsPayment(PersonAgent p, double price);
	
	public abstract void butYouOweMeOne(PersonAgent p);
}
