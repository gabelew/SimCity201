package city.gui;

import restaurant.Restaurant;
import javax.swing.*;
import city.MarketAgent;
import city.PersonAgent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class InfoPanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 1;
	private static final int NCOLUMNS = 1;
	private static final int NROWS = 2;
	private static final double PERSONS_DEFAULT_CASH = 200.00;
	private static final double NO_CASH = 0.0;

    private ListPanel personPanel = new ListPanel(this, "Persons");
    
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui

    public InfoPanel(SimCityGui gui) {

        //markets.add(new MarketAgent("Vons Market"));
        //markets.add(new MarketAgent("Sprouts Market"));
        //markets.add(new MarketAgent("CostCo"));
        
        this.gui = gui;

        setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));

        add(personPanel);

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
       public void addPerson(String type, String name) {

    	if (type.equals("Persons")) {
    		PersonAgent p = null;
    		if(stringIsDouble(name)){
    			p = new PersonAgent(name, Double.valueOf(name),gui);
    		}else if(name.toLowerCase().contains("rami") || name.toLowerCase().contains("mahdi") 
    				|| name.toLowerCase().contains("ditch") || name.toLowerCase().contains("broke")){
    			p = new PersonAgent(name, NO_CASH,gui);   			
    		}else{
    			p = new PersonAgent(name, PERSONS_DEFAULT_CASH, gui);
    		}
    		
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
    		}
    		
    		
    		
    		
    		
    		
    		
    		PersonGui g = new PersonGui(p, gui);
    		g.setPresent(true);
    		gui.animationPanel.addGui(g);// dw
    		
    		for(Restaurant r: gui.getRestaurants()){
    			p.addRestaurant(r);
    		}
    		
    		/*for(Home h : gui.getHomes())
    		{
    			if(h.people.size() == 0 && gui.getHomes().indexOf(h) != 32)
    			{
    			p.setHome(h);
    			h.addPerson(p);
    			System.out.println("my home is house #" + gui.getHomes().indexOf(h) +" " + gui.getHomes().size());
    			}
    			if(gui.getHomes().indexOf(h) == 31)
    			{
    				p.setHome(h);
        			h.addPerson(p);
    			}
    		}*/
    		p.setGui(g);
    			
    		p.startThread();
    		gui.persons.add(p);
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
    	/*if(tablesPanel.getPauseButtonLabel() == "Pause")
    	{
    	 for (CustomerAgent temp: customers)
    	 {
             temp.pauseAgent();
    	 }
    	 for (WaiterAgent temp: waiters) 
    	 {
             temp.pauseAgent();
    	 }
    	 for (MarketAgent temp: markets) 
    	 {
             temp.pauseAgent();
    	 }
    	 //host.pauseAgent();
    	 //cook.pauseAgent();
    	 //cashier.pauseAgent();
    	}
    	else
    	{
       	 for(CustomerAgent temp: customers)
       	 {
                temp.resumeAgent();
       	 }
       	 for (WaiterAgent temp: waiters)
       	 {
                temp.resumeAgent();
       	 }
       	 for (MarketAgent temp: markets)
       	 {
                temp.resumeAgent();
       	 }
       //	 host.resumeAgent();
       	// cook.resumeAgent();
    	 //cashier.resumeAgent();
    	}
    	 tablesPanel.changePauseButton();     
    */}

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

}

