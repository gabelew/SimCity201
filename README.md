Team 09
======

Fall 2013 SimCity201 Project Repository

####Team Members
| No. | Name |       USC Email       |                GitHub Username                |    
| :-: | :--------------------------- | :-------------------- | :-------------------------------------------- | 
|  1  | Gabriel Lew    | gabelew@usc.edu      | @[gabelew](https://github.com/gabelew)    | 
|  2  | Chad Martin    | chadmart@usc.edu       | @[chadmart](https://github.com/chadmart)        | 
|  3  | Emily Bernstein | ebernste@usc.edu   | @[theemilyjane](https://github.com/theemilyjane)   | 
|  4  | Gerson Hernandez | gersonh@usc.edu   | @[Gersonh](https://github.com/Gersonh)        | 
|  5  | Garland Chen    | garlandc@usc.edu    | @[garland106](https://github.com/garland106)  |

###Useful Testing Hacks
  + If a person's name contains "poor", the person will have 50 cash on hand and 200 in bank account
  + If a person's name contains "broke", the person will have 0 cash on hand and 0 in bank account
  + If a person's name contains "rich", the person will have 110 cash on hand and 10,000 in bank account
  + If a person's name does not contain "broke", "poor", or "rich, the person will have 99 cash on hand and 800 in bank account
  + If a person's name contains "Home", the person will have a house instead of an apartment
  + If a person's name does not contain "home", the person will live in an apartment as a renter unless there are no more apartments left
  + If a person's name contains "waiter" and "day" and "01"-"05", the person will work as a waiter during the day at the respective restaurant
  + If a person's name contains "waiter" and "night" and "01"-"05", the person will work as a waiter during the night at the respective restaurant
  + If a person's name contains "NoFood", the person will have no food in his fridge
  + •	If a person’s name contains “visitor” they will eat at home first, then visit all the work places.

###Sim City Rules
  + All people pay rent ($3) every 2 hours if they are a renter 
  + All people get paid ($10) every 2 hours if they are a worker
  + Workers either take the bus, drive, or walk to work
  

 ***

###Scenario A: Tests all behaviors
1. Pick a day, and Press Start! 
2. All workers go to their workplaces (if open) and start to work. 
3. visiterhome06LowSteak eats at home, then walks to the bank, market, and restaurant. 
To view his log, type his name into the Person Filter. 
Make sure the Person Alert Tag is turned on. He is in the sixth home (the first row, 6th from the left). 5. His name appears in the panel in his home.
6. Each intersection has a two way stop (both streets are one way). 

###Scenario B: Tests all behaviors
1. Pick a day, and Press Start! 
2. All workers go to their workplaces (if open) and start to work. 
3. visiterhome06LowSteak eats at home, then walks to the bank, market, and restaurant. 
To view his log, type his name into the Person Filter. Make sure the Person Alert Tag is turned on. He is in the sixth home (the first row, 6th from the left). His name appears in the panel in his home.
4. visiterhomebus07LowSteak eats at home, then takes the bus to the bank, market, and a restaurant. 
To view his log, type his name into the Person Filter. Make sure the Person Alert Tag is turned on. 
He is in the seventh home (the first row, 7th from the left). His name appears in the panel in his home.
5. visiterhomecar08LowSteak eats at home, then drives his car to the bank, market, and a restaurant. 
To view his log, type his name into the Person Filter. Make sure the Person Alert Tag is turned on. He is in the eighth home (the first row, 8th from the left). His name appears in the panel in his home.

###Scenario C: Tests cook, cashier, market interaction
1. Each restaurant is low on at least one item. When the cook gets to the restaurant, he will order from the closest open market. 
2. For instance, Restaurant 02 (1st level, 2nd from left) cook will order 50 chicken when he gets to work. The delivery man (maroon person) from the market next door will pick up the order from the shelf and walk outside to his yellow delivery van. The delivery van will drive to the market. The delivery man won’t leave until the bill has been paid. The cashier won’t pay him until he gets the invoice from the cook.  The cashier, delivery man, and cook who are in this scenario are posted below.

deliveryMan02daycar. Put the name into the person filter to see his log. Make sure the Market deliveryMan alert tag is turn on.

* Received Order
* Giving order to cook
* Received payment for order

cook02day. Put the name into the person filter to see his log. Make sure the Restaurant Cook alert tag is turned on.

* Ordering from market
* Received order from market

cashier02day. Put the name into the person filter to see his log. Make sure the Restaurant Cashier alert tag is turned on.

* Received bill from Market.
* Received invoice from cook.
* Payed bill from market
	
###Scenario E: Shows bus-stop behavior
1. People go to the bus stops at either end of the city to get on the bus. Bus travel is only for going up or down, not left to right. 
2. Many people get on the bus, but visiterhomebus07LowSteak always uses the bus. By looking at his log (make sure Person tag is on) you will see:

* I’m getting on the bus
* I’m getting off the bus
	
###Scenario F: Shows that people know they can't visit certain workplaces
1. Any workplace can be closed by pressing the close? checkbox by the name. For example, close all banks, press start, and all people should not go into banks. If it is closed, people will stop going, but if they are on their way when it closed, the go, see it is closed, then leave.

visiterhome06LowSteak

* eatin at home
* All banks are closed. I will try again when they are open
* I’m going to market 01
* I’m at market
* I’m going to restaurant 02
* I’m at Restaurant
	
###Scenario G: Tests market behavior
1. ***To see more clearly, we recommend closing all the other restaurants.
2. Market delivery fails if after the cook places the order, the restaurant closes. To do this, after cook02day puts “Ordering from market” in his log, close the fourth restaurant. The delivery van will go, see it is closed and output “Delivery failed because restaurant closed” 

cook02day

* Ordering from Market
* Received order form Market

deliveryMan01daycar

* Received order
* Delivery failed because restaurant closed
* Restaurant opened. Trying to deliver again
* Giving order to cook
* Received payment for order
	
###Scenario J: Normative Scenarios
1. There are more than 50 people in the city, there are 50 people working at the restaurants alone. Each restaurant has 2 waiters, 1 cook, 1 cashier, and 1 host by default, and each restaurant has 2 shifts. 
There are 5 restaurants (one for each team member) 8 banks and 6 markets (with 1 delivery man and 1 clerk each). If name has poor, they will eat at home, if not they will go out to eat. 
Vehicles stop at the stop signs. People don’t cross the street, they are safe and take the bus.

###Scenario K: Animation Details
1. Each house has the name of the occupant, and the apartments have the names of the occupants in them. 

###Scenario L: Gui Controls
1. Most all scenarios can be run at the same time, by opening and closing restaurants during the run. Bank robbing only occurs on weekends when the banks are closed. 
To change inventories in markets, open a market, and type in all of the amounts you want to change them to. Will update that markets inventory only.
Add people in the city on the city view by typing in a name and pressing add. Name hacks are stated above.

###Scenario M: Runtime Behavior
1. We have a grid of semaphores for the city view
2. Animation does not freeze
3. No known error exceptions
4. Agents don't bump into buildings. Within buildings, agents may move over objects. They know parkour.
5. Moving objects move on appropriate venues. Cars only move on roads and people only on crosswalks. They do not cross the streets.
6. All vehicles (buses and cars) stop at intersections.

###Scenario O: Bank Robbery
1. Turn on Person Alert Tag and type into filter crook01car
2. Start SimCity with Saturday.
3. Alert log shows which bank he is going to rob. Bank 00 and 01 are on row 1, 02 and 03 on row2 and so forth.
4. Click on the bank listed. When he says "I'm at bank," Bank Robber should go into bank and begin hacking ATMs.
5. He tries each ATM, with the message "Attempting robbery". He sends a hacking algorithm to the bank, and if the bank's defense algorithms
have a loophole that matches the hacking algorithm, he will succeed. The message should be something like "Hack succeeded! Stole $X". The
stolen amount depends on the hacking algorithm used.
6. If he does not succeed at the ATM, he goes to the next one until all ATMS have been attempted. The message for failure is
"Hack failed against bank's defense algorithms. Trying next ATM."
7. Since over half the city pays rent, the managers pay their employees and the bank handles those transfers, the hacker may take longer while infiltrating the flooded
bank system.
8. You may also filter back to Everyone and turn on the Bank System tag to see the bank responding to the hack.

###Scenario P: Vehicle accident
1. Buses like to run the stop signs. If they run the stop sign and hit a car, the person driving the car will give money to their insurance man. Person who crashed will output the message below.

* I crashed, now I must fix my car.
	
###Scenario Q: Vehicle hits pedestrian
1. Pedestrians don’t cross the street, they take buses. They want to make sure they are safe. Our city doesn't allow for crossing of streets.

###Scenario R: Weekend behavior is different
1. Banks are closed on weekends. People avoid banks on the weekends.

###Scenario S: Job Changing Shifts
1. At time 10 they will all start switching shifts (restaurant and markets). They day people will leave, and night will come. Easiest to verify is waiters. At time 10, go to the first restaurant. Employees should start leaving when a replacement comes in. 
Time is outputted on the log when person filter is Everyone

***

###Participation from each Member
  + Chad Martin: CardLayout, Person Agent, Person Agent tests, City Animation Panel, Add tables, Gui Interaction Panel, photoshopping of images, interaction diagrams
  + Gabriel Lew: Bank Agent, Bank Customer Role, Bank tests, Bank Animation, Manager employees payment behavior in Person Agent, landlord renter in person agent, general structure of wiki and images, Bank Robbery Scenario, RevolvingStandMonitor and Producer-Consumer base code and tests, Restaurant integration
  + Gerson Hernandez: Bus Agent, Bus Gui, bus tests, apartment animation layout
  + Emily Bernstein: Market Agent, Market Customer Role, Clerk Role, Delivery Man Role, Delivery Man and Clerk Gui, Delivery Man Driving Gui, Market Panel, Market tests(including market roles)
  + Garland Chen: AtHome role, AtHome tests, RepairMan role, Apartment Gui && animation upgrades, AtHome gui, Home, Residence
