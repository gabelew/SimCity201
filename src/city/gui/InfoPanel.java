package city.gui;

import restaurant.Restaurant;

import javax.swing.*;

import bank.BankBuilding;
import atHome.city.Apartment;
import atHome.city.Home;
import atHome.city.Residence;
import city.MarketAgent;
import city.PersonAgent;
import city.roles.BankCustomerRole;
import city.roles.Role;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class InfoPanel extends JPanel implements KeyListener,ActionListener {
	private static final long serialVersionUID = 1L;
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 1;
	private static final int NCOLUMNS = 1;
	private static final int NROWS = 2;
	private static final double PERSONS_DEFAULT_CASH = 99.00;
	private static final double NO_CASH = 0.0;

    private ListPanel personPanel = new ListPanel(this, "Persons");

    private JPanel group = new JPanel();
    
    private JPanel configPanel = new JPanel();
    private JButton goButton = new JButton("Start");
    private String[] dayStrings = {"Monday", "Tuesday", "Wednesday", "Thrusday", "Friday", "Saturday", "Sunday"};
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private JComboBox configList = new JComboBox(dayStrings);

    private SimCityGui gui; //reference to main gui

    public InfoPanel(SimCityGui gui) {
        
        this.gui = gui;

        this.configPanel.setLayout(new BorderLayout(5,0));
        configPanel.add(this.configList, BorderLayout.CENTER);
        configPanel.add(this.goButton, BorderLayout.EAST);
        
        setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        //group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));
        group.setLayout(new FlowLayout());
        
        add(personPanel);
        group.add(configPanel);
        add(group);
        
        personPanel.hidePauseButton();

        goButton.addActionListener(this);
        personPanel.getTypeNameHere().addKeyListener(this);
    }

    public void setHungry(String type, String name) {

        if (type.equals("Persons")) {

            for (PersonAgent temp: gui.persons){
                if (temp.getName() == name){}
                	//temp.getGui().setHungry();
            }
        }
    }
    
/*    public void setWorking(String type, String name) {

        if (type.equals("Waiters")) {

            for (WaiterAgent temp: waiters) {
                if (temp.getName() == name)
                {
                	temp.getGui().setWorking();
                }
            }
        }
    }

	public void askBreak(String name) {
        for (WaiterAgent temp: waiters) {
            if (temp.getName() == name)
            	temp.getGui().askBreak();
        }
	}*/
    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    private  Residence findOpenHome(String name){
		if(gui.apartmentsAvaiable() && !(name.toLowerCase().contains("home")) ){
			for(Apartment a: gui.apartments){
				if(a.noVacancies == false){
					/*p.setHome(a);
					a.addRenter(p);
        			break;*/
					return a;
				}
			}
		}else if(gui.persons.size() <= 160){
			for(Home h : gui.getHomes())
    		{
        		if(h.owner == null){
        			/*
        			p.setHome(h);
        			h.owner = p;
        			break;*/
        			return h;
    			}
    		}
			for(Apartment a: gui.apartments){
				if(a.noVacancies == false){
					/*p.setHome(a);
					a.addRenter(p);
        			break;*/
					return a;
				}
			}
		}
		return null;
    }
       public void addPerson(String type, String name) {

    	if (type.equals("Persons")) {
    		PersonAgent p = null;
    		Residence residence = findOpenHome(name);
    		if(stringIsDouble(name)){
    			p = new PersonAgent(name, Double.valueOf(name),gui, residence);
    			BankCustomerRole bcr = null;
    			for(Role role : p.roles) {
    				if(role instanceof BankCustomerRole)
    					bcr = (BankCustomerRole)role;
    			}
    			gui.bankAgent.msgOpenAccount(bcr, Double.valueOf(name), "personal");
    		}else if(name.toLowerCase().contains("rami") || name.toLowerCase().contains("mahdi") 
    				|| name.toLowerCase().contains("ditch") || name.toLowerCase().contains("broke")){
    			p = new PersonAgent(name, NO_CASH,gui, residence);   
    			BankCustomerRole bcr = null;
    			for(Role role : p.roles) {
    				if(role instanceof BankCustomerRole)
    					bcr = (BankCustomerRole)role;
    			}
    			gui.bankAgent.msgOpenAccount(bcr, 0, "personal");
    		}else if(name.toLowerCase().contains("poor")){
    			p = new PersonAgent(name, 50,gui, residence);
    			BankCustomerRole bcr = null;
    			for(Role role : p.roles) {
    				if(role instanceof BankCustomerRole)
    					bcr = (BankCustomerRole)role;
    			}
    			gui.bankAgent.msgOpenAccount(bcr, 200, "personal");
    		} else if(name.toLowerCase().contains("rich")) {
    			p = new PersonAgent(name, 110,gui, residence);
    			BankCustomerRole bcr = null;
    			for(Role role : p.roles) {
    				if(role instanceof BankCustomerRole)
    					bcr = (BankCustomerRole)role;
    			}
    			gui.bankAgent.msgOpenAccount(bcr, 10000, "personal");
    		} else{
    			p = new PersonAgent(name, PERSONS_DEFAULT_CASH, gui, residence);
    			BankCustomerRole bcr = null;
    			for(Role role : p.roles) {
    				if(role instanceof BankCustomerRole)
    					bcr = (BankCustomerRole)role;
    			}
    			gui.bankAgent.msgOpenAccount(bcr, 800, "personal");
    		}
    		
    		
    		if(residence instanceof Apartment){
    			((Apartment)residence).addRenter(p);
    			p.isRenter = true;
    		}else{
    			((Home)residence).owner = p;
    		}
    		
    		p.addAtHomeRole();
    		
    		if(name.toLowerCase().contains("waiter") && name.toLowerCase().contains("day")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.day);
    				
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.day);
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.day);
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.day);
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.day);
    			}
    			
    		}
    		else if(name.toLowerCase().contains("waiter") && name.toLowerCase().contains("night")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.night);
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.night);
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.night);
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.night);
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				p.job = p.new MyJob(r.location , "waiter", PersonAgent.Shift.night);
    			}
    		}else if(name.toLowerCase().contains("host") && name.toLowerCase().contains("day")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("host")){
    		    				System.out.println("already has host");
    							hasHost = true;
    						}
    					}
    				}
    				if(!hasHost){
    	    			System.out.println("making host");
    					p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.day);
    				}
        			
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}	
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.day);
					}
    			}
    		}else if(name.toLowerCase().contains("host") && name.toLowerCase().contains("night")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}    					
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}    					
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasHost = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("host")){
    							hasHost = true;
    						}
    					}    					
        			}
    				if(!hasHost){
						p.job = p.new MyJob(r.location , "host", PersonAgent.Shift.night);
					}
    			}
    		}else if(name.toLowerCase().contains("cook") && name.toLowerCase().contains("day")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.day);
					}
    			}
    		}else if(name.toLowerCase().contains("cook") && name.toLowerCase().contains("night")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasCook = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cook")){
    							hasCook = true;
    						}
    					}    					
        			}
    				if(!hasCook){
						p.job = p.new MyJob(r.location , "cook", PersonAgent.Shift.night);
					}
    			}
    		}else if(name.toLowerCase().contains("cashier") && name.toLowerCase().contains("day")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}    					
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.day);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.day);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.day);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.day);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.day);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}    			
    		}else if(name.toLowerCase().contains("cashier") && name.toLowerCase().contains("night")){
    			if(name.toLowerCase().contains("01")){
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.night);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("02")){
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
    					
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.night);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("03")){
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.night);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("04")){
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.night);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}else if(name.toLowerCase().contains("05")){
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasCashier = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("cashier")){
    							hasCashier = true;
    						}
    					}
        			}
    				if(!hasCashier){
						p.job = p.new MyJob(r.location , "cashier", PersonAgent.Shift.night);
						p.businessAccount = r.getRestaurantAccount();
					}
    			}
    		}else if(name.toLowerCase().contains("clerk") && name.toLowerCase().contains("day")){
    			if(name.toLowerCase().contains("01")){
    				MarketAgent m = gui.markets.get(0);
    				boolean hasMarket = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasMarket = true;
    						}
    					}    					
        			}
    				if(!hasMarket){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("02")){
    				MarketAgent m = gui.markets.get(1);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("03")){
    				MarketAgent m = gui.markets.get(2);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("04")){
    				MarketAgent m = gui.markets.get(3);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("05")){
    				MarketAgent m = gui.markets.get(4);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.day);
					}
    			}    			
    		}else if(name.toLowerCase().contains("clerk") && name.toLowerCase().contains("night")){
    			if(name.toLowerCase().contains("01")){
    				MarketAgent m = gui.markets.get(0);
    				boolean hasMarket = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasMarket = true;
    						}
    					}    					
        			}
    				if(!hasMarket){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("02")){
    				MarketAgent m = gui.markets.get(1);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("03")){
    				MarketAgent m = gui.markets.get(2);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("04")){
    				MarketAgent m = gui.markets.get(3);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("05")){
    				MarketAgent m = gui.markets.get(4);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasClerk = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("clerk")){
    							hasClerk = true;
    						}
    					}
        			}
    				if(!hasClerk){
						p.job = p.new MyJob(m.location , "clerk", PersonAgent.Shift.night);
					}
    			}    			
    		}else if(name.toLowerCase().contains("deliveryman") && name.toLowerCase().contains("day")){
    			if(name.toLowerCase().contains("01")){
    				MarketAgent m = gui.markets.get(0);
    				boolean hasMarket = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasMarket = true;
    						}
    					}    					
        			}
    				if(!hasMarket){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("02")){
    				MarketAgent m = gui.markets.get(1);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("03")){
    				MarketAgent m = gui.markets.get(2);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("04")){
    				MarketAgent m = gui.markets.get(3);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("05")){
    				MarketAgent m = gui.markets.get(4);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.day && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.day);
					}
    			}    			
    		}else if(name.toLowerCase().contains("deliveryman") && name.toLowerCase().contains("night")){
    			if(name.toLowerCase().contains("01")){
    				MarketAgent m = gui.markets.get(0);
    				boolean hasMarket = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasMarket = true;
    						}
    					}    					
        			}
    				if(!hasMarket){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("02")){
    				MarketAgent m = gui.markets.get(1);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("03")){
    				MarketAgent m = gui.markets.get(2);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("04")){
    				MarketAgent m = gui.markets.get(3);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("05")){
    				MarketAgent m = gui.markets.get(4);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}else if(name.toLowerCase().contains("06")){
    				MarketAgent m = gui.markets.get(5);
    				boolean hasdeliveryMan = false;
    				for(PersonAgent currentP: gui.persons){
    					if(currentP.job!=null){
    						if(currentP.job.location == m.location && currentP.job.shift == PersonAgent.Shift.night && currentP.job.type.equalsIgnoreCase("deliveryman")){
    							hasdeliveryMan = true;
    						}
    					}
        			}
    				if(!hasdeliveryMan){
						p.job = p.new MyJob(m.location , "deliveryman", PersonAgent.Shift.night);
					}
    			}    			
    		} else if(name.toLowerCase().contains("rmanager")) {
    			if(name.toLowerCase().contains("01")) {
    				Restaurant r = gui.restaurants.get(0);
    				boolean hasManager = false;
    				for(PersonAgent currentP: gui.persons) {
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.type.equalsIgnoreCase("manager")){
    							hasManager = true;
    						}
    					}
    				}
    				if(!hasManager) {
    					p.job = p.new MyJob(r.location, "manager", PersonAgent.Shift.none);
    					p.isManager = true;
    					BankCustomerRole bcr = null;
    					for(Role role : p.roles) {
    						if(role instanceof BankCustomerRole)
    							bcr = (BankCustomerRole)role;
    					}
    					gui.bankAgent.msgOpenAccount(bcr, 8000, "business");
    					r.setRestaurantAccount(p.businessAccount);
    				}
    			} else if(name.toLowerCase().contains("02")) {
    				Restaurant r = gui.restaurants.get(1);
    				boolean hasManager = false;
    				for(PersonAgent currentP: gui.persons) {
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.type.equalsIgnoreCase("manager")){
    							hasManager = true;
    						}
    					}
    				}
    				if(!hasManager) {
    					p.job = p.new MyJob(r.location, "manager", PersonAgent.Shift.none);
    					p.isManager = true;
    					BankCustomerRole bcr = null;
    					for(Role role : p.roles) {
    						if(role instanceof BankCustomerRole)
    							bcr = (BankCustomerRole)role;
    					}
    					gui.bankAgent.msgOpenAccount(bcr, 8000, "business");
    					r.setRestaurantAccount(p.businessAccount);
    				}
    			} else if(name.toLowerCase().contains("03")) {
    				Restaurant r = gui.restaurants.get(2);
    				boolean hasManager = false;
    				for(PersonAgent currentP: gui.persons) {
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.type.equalsIgnoreCase("manager")){
    							hasManager = true;
    						}
    					}
    				}
    				if(!hasManager) {
    					p.job = p.new MyJob(r.location, "manager", PersonAgent.Shift.none);
    					p.isManager = true;
    					BankCustomerRole bcr = null;
    					for(Role role : p.roles) {
    						if(role instanceof BankCustomerRole)
    							bcr = (BankCustomerRole)role;
    					}
    					gui.bankAgent.msgOpenAccount(bcr, 8000, "business");
    					r.setRestaurantAccount(p.businessAccount);
    				}
    			} else if(name.toLowerCase().contains("04")) {
    				Restaurant r = gui.restaurants.get(3);
    				boolean hasManager = false;
    				for(PersonAgent currentP: gui.persons) {
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.type.equalsIgnoreCase("manager")){
    							hasManager = true;
    						}
    					}
    				}
    				if(!hasManager) {
    					p.job = p.new MyJob(r.location, "manager", PersonAgent.Shift.none);
    					p.isManager = true;
    					BankCustomerRole bcr = null;
    					for(Role role : p.roles) {
    						if(role instanceof BankCustomerRole)
    							bcr = (BankCustomerRole)role;
    					}
    					gui.bankAgent.msgOpenAccount(bcr, 8000, "business");
    					r.setRestaurantAccount(p.businessAccount);
    				}
    			} else if(name.toLowerCase().contains("05")) {
    				Restaurant r = gui.restaurants.get(4);
    				boolean hasManager = false;
    				for(PersonAgent currentP: gui.persons) {
    					if(currentP.job!=null){
    						if(currentP.job.location == r.location && currentP.job.type.equalsIgnoreCase("manager")){
    							hasManager = true;
    						}
    					}
    				}
    				if(!hasManager) {
    					p.job = p.new MyJob(r.location, "manager", PersonAgent.Shift.none);
    					p.isManager = true;
    					BankCustomerRole bcr = null;
    					for(Role role : p.roles) {
    						if(role instanceof BankCustomerRole)
    							bcr = (BankCustomerRole)role;
    					}
    					gui.bankAgent.msgOpenAccount(bcr, 8000, "business");
    					r.setRestaurantAccount(p.businessAccount);
    				}
    			}
    		} else if(name.toLowerCase().contains("landlord")) {
    			p.job = p.new MyJob("landlord");
    		}
    		
    		
    		PersonGui g = new PersonGui(p, gui);
    		g.setPresent(true);
    		gui.animationPanel.addGui(g);// dw
    		
    		for(Restaurant r: gui.getRestaurants()){
    			p.addRestaurant(r);
    		}
    		for(MarketAgent m: gui.getMarkets()){
    			p.addMarket(m);
    		}
    		for(BankBuilding b: gui.getBanks()){
    			p.addBank(b);
    		}
    		
    		p.setGui(g);
    			
    		gui.persons.add(p);
    		p.startThread();
    	}
    }
 /*  public void setCustomerEnabled(CustomerAgent c){
    	personPanel.setCustomerEnabled(c.getName());
    }
	public void setTableEnabled(int tableNumber){
		tablesPanel.setTableEnabled(tableNumber);
	}

	public void setTableDisabled(int tableNumber){
		tablesPanel.setTableDisabled(tableNumber);
	}*/

	
    public void pauseAgents()
    {
    	if(personPanel.getPauseButtonLabel() == "Pause")
    	{
    	 for (PersonAgent temp: gui.persons)
    	 {
             temp.pauseAgent();
    	 }
    	 for (MarketAgent temp: gui.markets) 
    	 {
             temp.pauseAgent();
    	 }
    	 gui.animationPanel.busLeft.pauseAgent();
    	 gui.animationPanel.busRight.pauseAgent();
    	 gui.bankAgent.pauseAgent();
    	 gui.animationPanel.paused = true;
    	}
    	else
    	{
       	 for(PersonAgent temp: gui.persons)
       	 {
                temp.resumeAgent();
       	 }
       	 for (MarketAgent temp: gui.markets)
       	 {
                temp.resumeAgent();
       	 }
    	 gui.animationPanel.busLeft.resumeAgent();
    	 gui.animationPanel.busRight.resumeAgent();
    	 gui.bankAgent.resumeAgent();
    	 gui.animationPanel.paused = false;
    	}
    	personPanel.changePauseButton();     
    }

	public void setWaiterOnBreak(String name) {
		//waitersPanel.setWaiterOnBreak(name);
	}

	public void setWaiterCantBreak(String name) {
		//waitersPanel.setWaiterCantBreak(name);
		
	}

	public void setWaiterBreakable(String name) {
		//waitersPanel.setWaiterBreakable(name);
		
	}
	public void setWaiterUnbreakable(String name) {
		//waitersPanel.setWaiterUnbreakable(name);
		
	}

	public boolean stringIsDouble(String myString){
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
             "[+-]?(" + // Optional sign character
             "NaN|" +           // "NaN" string
             "Infinity|" +      // "Infinity" string

             // A decimal floating-point string representing a finite positive
             // number without a leading sign has at most five basic pieces:
             // Digits . Digits ExponentPart FloatTypeSuffix
             // 
             // Since this method allows integer-only strings as input
             // in addition to strings of floating-point literals, the
             // two sub-patterns below are simplifications of the grammar
             // productions from the Java Language Specification, 2nd 
             // edition, section 3.10.2.

             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

             // . Digits ExponentPart_opt FloatTypeSuffix_opt
             "(\\.("+Digits+")("+Exp+")?)|"+

       // Hexadecimal strings
       "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +

        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

        ")[pP][+-]?" + Digits + "))" +
             "[fFdD]?))" +
             "[\\x00-\\x20]*");// Optional trailing "whitespace"
            
        if (Pattern.matches(fpRegex, myString))
	            return true;//Double.valueOf(myString); // Will not throw NumberFormatException
        else {
	        	return false;
        }	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		/*if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            cook.badSteaks();
        }
		
		if ((e.getKeyCode() == KeyEvent.VK_2) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            cook.cookieMonster();
        }

		if ((e.getKeyCode() == KeyEvent.VK_D) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            markets.get(0).msgTossEverythingButCookies();
        }
		
		if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			cook.setSteaksAmount(5);
        }

		if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			
			for(MarketAgent m: markets){
				System.out.println("\nMarket Inventory: " + m.getName());
				for(MarketAgent.MyFood f: m.foods){
					String mstate = null;
					for(CookAgent.MyMarket mm: cook.markets){
						if(mm.getMarket() == m)
						{
							mstate = mm.foodInventoryMap.get(f.getChoice()).toString();
						}
					}
					System.out.print("\t" + f.getChoice() + " " + f.getAmount() + " "+ mstate + "\t");
				}
				System.out.println(" ");
			}
        }

		if ((e.getKeyCode() == KeyEvent.VK_E) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				System.out.println("Cook Inventory: ");
				for(CookAgent.Food f: cook.foods){
					System.out.print("\t" + f.getChoice() + "\t" + f.getAmount() + "\t");
				}
				System.out.println(" ");
        }
	*/	
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}
	@Override
	public void keyTyped(KeyEvent e) {		
	}
	
	public ListPanel getPersonPanel(){
		return personPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String configSetting = (String) configList.getSelectedItem();
		gui.dayOfWeek = configSetting;
		configPanel.removeAll();
		configPanel.invalidate();
		configPanel.validate();
		remove(configPanel);
		pauseAgents();
		personPanel.showPauseButton();
		gui.invalidate();
		gui.validate();
	}

}

