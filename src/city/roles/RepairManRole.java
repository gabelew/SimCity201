package city.roles;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import atHome.city.Apartment;
import city.PersonAgent;
import city.PersonAgent.Location;
import city.animationPanels.ApartmentAnimationPanel;
import city.animationPanels.HouseAnimationPanel;
import city.gui.Gui;
import city.gui.RepairManDrivingGui;
import city.gui.RepairManGui;
import city.gui.SimCityGui;
import city.interfaces.RepairMan;

public class RepairManRole extends Role implements RepairMan
{
/*********************
 ***** DATA
 ********************/
		List<Job> jobs = new ArrayList<Job>();
		Map<String, Double> pricingMap = new HashMap<String, Double>();
		enum JobState {none, requested, inProgess, awaitingPayment, paid}
		public SimCityGui gui;
		
		//animation stuff
		RepairManDrivingGui repairmanDrivingGui;
		RepairManGui repairmanGui;
		private Semaphore driving = new Semaphore(0,true);
		private Timer timer = new Timer();
		final int FIXTIME = 2000;
		
		//default constructor
		public RepairManRole(PersonAgent p, SimCityGui g)
		{
			super(p);
			pricingMap.put("fridge", new Double(150));
			pricingMap.put("sink", new Double(85));
			pricingMap.put("stove", new Double(100));
			
			repairmanGui = new RepairManGui(this);
			this.gui = g;
			repairmanDrivingGui = new RepairManDrivingGui(this, gui);
			
			gui.animationPanel.addGui((Gui)repairmanDrivingGui);//adds driving truck to gui
		}
		
		public void setGui(Gui gui) 
		{
			//this.repairmanGui = (RepairManDrivingGui) gui;
		}
		public Gui getGui() 
		{
			return repairmanGui;
		}

		
/*********************
 ***** MESSAGES
 ********************/
		
		//adds a new order to fix
		public void fixAppliance(AtHomeRole role, String app)
		{
			print("!@#$ I have a job to do");
			jobs.add(new Job(role,app,role.myPerson.myHome.location));
			myPerson.msgStartRepairJob();
			stateChanged();
		}
		
		//Customer bank transfers money over
		public void HereIsPayment(AtHomeRole role)
		{
			for(Job j : jobs)
			{
				if(j.person.equals(role))
				{
					j.state = JobState.paid;
					break;
				}
			}
			stateChanged();
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
			stateChanged();
		}
/*********************
 ***** ACTIONS
 ********************/
	public void StartJob(final Job j)
	{
		//*
		//goes to customer location
		print("leaving home");
		addGuiForJob(j);
		repairmanGui.leaveHome();
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		//drives to customer location
		print("driving to customer");
		repairmanDrivingGui.DoGoFix(j.location);
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		//enters customer home
		print("at customer home");
		repairmanGui.goToCustomer();
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		print("leaving customer home");
		repairmanGui.leaveHome();
		try { driving.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		j.state = JobState.inProgess;
		
		timer.schedule(new TimerTask() {
			public void run() 
			{
				print(j.person.myPerson.getName() + " I sent the bill, wire me the money");
				j.state = JobState.awaitingPayment;
				j.person.ApplianceFixed(j.appliance, pricingMap.get(j.appliance).doubleValue());
				
				//leaves
				repairmanDrivingGui.DoGoBack();
				try { driving.acquire();} 
				catch (InterruptedException e) {e.printStackTrace();}
				
			}
		},
		FIXTIME);
		
	}
	
	public void ProcessPayment(Job j)
	{
		print("done with job for, " + j.person.myPerson.getName());
		jobs.remove(j);
	}
	
/*********************
 ***** Animation Methods
 ********************/
	
	public void addGuiForJob(Job jb)
	{
		myPerson.myHome.insideAnimationPanel.addGui(repairmanGui);/*
		if(jb.person.myPerson.myHome instanceof Apartment)
		{
			((ApartmentAnimationPanel)
		}
		else
		{
			((HouseAnimationPanel)myPerson.myHome.insideAnimationPanel).addGui(repairmanGui);
		}*/
	}
	public void removeGuiForJob(Job jb)
	{
		if(jb.person.myPerson.myHome instanceof Apartment)
		{
			((ApartmentAnimationPanel)myPerson.myHome.insideAnimationPanel).removeGui(repairmanGui);
		}
		else
		{
			((HouseAnimationPanel)myPerson.myHome.insideAnimationPanel).removeGui(repairmanGui);
		}
	}
	public void msgActionDone() 
	{
		driving.release();
	}
	
/*********************
 ***** SCHEDULER
 ********************/
	public boolean pickAndExecuteAnAction() 
	{
		
		for(Job j : jobs)
		{
			if(j.state == JobState.paid)
			{
				ProcessPayment(j);
				return true;
			}
		}
		
		for(Job j : jobs)
		{
			if(j.state == JobState.requested)
			{
				StartJob(j);
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
