package city.interfaces;

import city.roles.AtHomeRole;

public interface RepairMan 
{
	public abstract void fixAppliance(AtHomeRole role, String app);
	
	public abstract void HereIsPayment(AtHomeRole role);
	
	public abstract void butYouOweMeOne(AtHomeRole role);
}
