package city.roles;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.PersonAgent.Location;
import city.gui.Gui;
import city.gui.RepairManDrivingGui;
import city.gui.RepairManGui;
import city.interfaces.RepairMan;

public class RepairManRole extends Role implements RepairMan
{
/*********************
 ***** DATA
 ********************/
		List<Job> jobs = new ArrayList<Job>();
		Map<String, Double> pricingMap = new HashMap<String, Double>();
		enum JobState {none, requested, inProgess, awaitingPayment, paid}
		
		//animation stuff
		RepairManDrivingGui repairmanDrivingGui;
		RepairManGui repairmanGui;
		private Semaphore driving = new Semaphore(0,true);
		private Timer timer = new Timer();
		final int FIXTIME = 2000;
		
		//default constructor
		public RepairManRole(PersonAgent p)
		{
			super(p);
			pricingMap.put("fridge", new Double(150));
			pricingMap.put("sink", new Double(85));
			pricingMap.put("stove", new Double(100));
		}
		
		public void setGui(Gui gui) 
		{
			//this.repairmanGui = (RepairManDrivingGui) gui;
		}
		public Gui getGui() 
		{
			return null;//repairmanGui;
		}

		
/*********************
 ***** MESSAGES
 ********************/
		
		//adds a new order to fix
		public void fixAppliance(AtHomeRole role, String app)
		{
			jobs.add(new Job(role,app,role.myPerson.myHome.location));
		}
		
		//Customer bank transfers money over
		public void HereIsPayment(AtHomeRole role, double price)
		{
			for(Job j : jobs)
			{
				if(j.person.equals(role))
				{
					j.state = JobState.paid;
					break;
				}
			}
		}
		
		//Lets it slide, person doesn't pay
		public void butYouOweMeOne(AtHomeRole role)
		{
			myPerson.print("Only because you're the only reason I passed 201");
			for(Job j : jobs)
			{
				if(j.person.equals(role))
				{
					jobs.remove(j);
					break;
				}
			}
		}
/*********************
 ***** ACTIONS
 ********************/
	public void StartJob(final Job j)
	{
		//goes to customer location
		repairmanGui.leaveHome();
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		//drives to customer location
		repairmanDrivingGui.DoGoFix(j.location);
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		//enters customer home
		repairmanGui.goToCustomer();
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		
		timer.schedule(new TimerTask() {
			public void run() 
			{
				print("!@#$%^& sent bill, wire me the money");
				j.state = JobState.awaitingPayment;
				j.person.ApplianceFixed(j.appliance, pricingMap.get(j.appliance).doubleValue());
				stateChanged();
			}
		},
		FIXTIME);
		
	}
	
	public void ProcessPayment(Job j)
	{
		jobs.remove(j);
	}
	
/*********************
 ***** Animation Methods
 ********************/
	public void msgActionDone() {
		driving.release();
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
				return true;
			}
		}
		
		for(Job j : jobs)
		{
			if(j.state == JobState.paid)
			{
				ProcessPayment(j);
				return true;
			}
		}
		return false;
	}
	
/*****************
 * Helper Classes
 *****************/
	class Job
	{
		AtHomeRole person;
		Point location;
		String appliance;
		JobState state = JobState.requested;
		public Job(AtHomeRole r, String app, Point loc)
		{
			this.location = loc;
			this.person = r;
			this.appliance = app;
		}
	}

}
