﻿package city;

import java.awt.Point;
import java.sql.Time;
import java.util.*;

import restaurant.HostAgent;
import agent.Agent;
import city.roles.*;

public class PersonAgent extends Agent {
	private List<Role> roles = new ArrayList<Role>();
	//hacked in upon creation
	private List<MyRestaurant> restaurants = new ArrayList<MyRestaurant>(); 
	
	enum State { doingNothing, goingToRestaurant, eating, working, shopping, banking, onWorkBreak, offWorkBreak };
	enum Event { none, goingToEatAtHome, goingToWork, goingOutToEat };
	enum Location { AtHome, AtWork, AtMarket, AtBank, InCity, AtRestaurant};
	enum TransportState { none, WalkingToDest, GoingToBus, WaitingForBus, OnBus, GettingOffBus, GoingToCar, OnCar, GettingOutOfCar};
	
	private String name;
	
	private State state = State.doingNothing; 
	private Event event = Event.none;
	private Location location = Location.AtHome;
	private TransportState transportState = TransportState.none;
	
	//Time currentTime;
	int currentHour;
	String dayOfWeek;
	
	int hungerLevel = 51;
	double cashOnHand;
	
	class MyRestaurant {
		//Restaurant r; 
		HostAgent h;
		Point location;
		String type;
		String name;
		MyRestaurant(HostAgent h, Point location, String type, String name) {
			this.h = h;
			this.location = location;
			this.type = type;
			this.name = name;
		}
	}
	        
	public PersonAgent(String name) {
	    this.name = name;
	}
	
	public void addRole(Role r) {
        roles.add(r);
        r.setPerson(this);
	}
	
	public void addRestaurant(HostAgent h, Point location, String type, String name) {
		restaurants.add(new MyRestaurant(h, location, type, name));
	}
	
	public void setName(String name){
        this.name = name;
	}
	
	public String getName(){
       return name;
	}
	
	public void msgNextHour(int hour, String dayOfWeek) {
		this.currentHour = hour;
		this.dayOfWeek = dayOfWeek;
		this.hungerLevel += 10;
	}
	
    public boolean pickAndExecuteAnAction() {
    	if(hungerLevel > 50 && state == State.doingNothing) {
    		goEatFood();
    	}
    	
        try {
        	if(state == State.eating && event == Event.goingOutToEat) {
        		state = State.goingToRestaurant;
        		goToRestaurant();
        	}
        	
	        for(Role r : roles) {
	        	if( r.isActive() ) {
	        		r.pickAndExecuteAnAction();
	        		return true;
	        	}
	        }
	        
	        return false;
        
        } catch(ConcurrentModificationException e){ return false; }
	}
    
    private void goEatFood() {
    	state = State.eating;
    	event = Event.goingOutToEat; // if out of food at home?
    }
    
    private void goToRestaurant() {
    	// DoGoToRestaurant animation
    	location = Location.AtRestaurant;
    	MyRestaurant mr = restaurants.get(0); // hack for first restaurant for now
    	//RestaurantCustomerRole rcr = roles.get(0); // hack 
    	//mr.h.msgIWantToEat(rcr);
    }
    
}