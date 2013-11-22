package city.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import city.PersonAgent;
import city.interfaces.RepairMan;

public class RepairManRole extends Role implements RepairMan
{
/*********************
 ***** DATA
 ********************/
		List<Job> jobs = new ArrayList<Job>();
		Map<String, Double> pricingMap = new HashMap<String, Double>();
		enum JobState {none, requested, inProgess, awaitingPayment}
		PersonAgent myPerson;
		public RepairManRole(PersonAgent p)
		{
			super(p);
		}
/*********************
 ***** MESSAGES
 ********************/
		public void fixAppliance(PersonAgent p, String app)
		{
			jobs.add(new Job(p,app));
		}
		
		public void HereIsPayment(PersonAgent p, double price)
		{
			myPerson.cashOnHand += price;
			for(Job j : jobs)
			{
				if(j.person.equals(p))
				{
					jobs.remove(j);
					break;
				}
			}
		}
		public void butYouOweMeOne(PersonAgent p)
		{
			myPerson.print("Only because you're the only reason I passed 201");
			for(Job j : jobs)
			{
				if(j.person.equals(p))
				{
					jobs.remove(j);
					break;
				}
			}
		}
/*********************
 ***** ACTIONS
 ********************/
	public void StartJob(Job j)
	{
		//DoGoToHouse();
		//DoFixApp(j.appliance);
		j.person.ApplianceFixed(j.appliance, pricingMap.get(j.appliance).doubleValue());
	}
/*********************
 ***** SCHEDULER
 ********************/
	public boolean pickAndExecuteAnAction() 
	{
		for(Job j : jobs)
		{
			if(j.state == JobState.requested)
			{
				StartJob(j);
			}
		}
		return false;
	}
	
/*****************
 * Helper Classes
 *****************/
	class Job
	{
		PersonAgent person;
		String appliance;
		JobState state = JobState.requested;
		public Job(PersonAgent p, String app)
		{
			this.person = p;
			this.appliance = app;
		}
	}

}
