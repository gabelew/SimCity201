Team 09
======

SimCity201 Project Repository for CS 201 students

####Team Members
| No. | Name |       USC Email       |                GitHub Username                |      Lecture Section      | USC ID |
| :-: | :--------------------------- | :-------------------- | :-------------------------------------------- | :------------- | :---------- |
|  1  | Gabriel Lew    | gabelew@usc.edu      | @[gabelew](https://github.com/gabelew)    | MW 4:00pm | 3356317145|
|  2  | Chad Martin    | chadmart@usc.edu       | @[chadmart](https://github.com/chadmart)        | MW 4:00pm  | 5493785402|
|  3  | Emily Bernstein | ebernste@usc.edu   | @[theemilyjane](https://github.com/theemilyjane)   |   MW 12:00pm  |5445682605|
|  4  | Gerson Hernandez | gersonh@usc.edu   | @[Gersonh](https://github.com/Gersonh)        |  MW 4:00pm   | 5994912227|
|  5  | Garland Chen    | garlandc@usc.edu    | @[garland106](https://github.com/garland106)  |MW 4:00pm|7276634816|

####Team Meetings
|       Meeting       |           Time           |      Location      |
| :------------------ | :----------------------- | :----------------- |
| Lab                 | Tues. 04:00pm             | SAL 123           |
| Weekly Meeting 1    | Thurs. 07:30pm to 10:00pm  | GFS114      |
| Weekly Meeting 2    | Fri. 06:00pm to 10:00pm  | KAP150      |

###Instructions
  + Move imgs directory to root of project, if not there import into eclipse so that imgs folder appears under root directory
  + Table, if not in use, can be moved using drag and drop.
  + A table is not in use if label is green  
  + A table is in use if label is red
  + Tables cannot be placed on top of each other
  + Tables can only be placed in valid area show by darker grey area when moving tables

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

###Sim City Rules
  + All people pay rent ($3) every 2 hours if they are a renter 
  + All people get paid ($10) every 2 hours if they are a worker
  + Workers either take the bus, drive, or walk to work
  
###People with compound names
	1. Person lives in apartment, and cooks food at home
	 . follow "poor01" 
     ... lives in the first apartment from top left, apt room #1 (top left room)
     . eats at home
     ... Messages:
     ....... "I'm going home to eat"
     ....... "I'm Eatin at Home"
     ....... "I'm hungry and want to make: "
     ....... "cooking food"
     ....... "food is done!!!"
	2. Person lives in house, cooks food at home
	 . follow "poorHome02" 
     . lives in the second home from top left
     . eats at home
     ... Messages:
     ....... "I'm going home to eat"
     ....... "I'm Eatin at Home"
     ....... "I'm hungry and want to make: "
     ....... "cooking food"
     ....... "food is done!!!"
	3. Person lives in home, has no food, so goes to bank and market and restocks food
	 . follow "poorHome04NoFood"
     . lives in the fourth home from top left
     . has no food, so decides to go to market
     ... Messages:
     ....... "I'm going home to eat"
     ....... "I'm Eatin at Home"
     ....... "I'm out of food"
     . goes to bank first to get money
     ... Messages:
     ....... "I'm going to bank"
     ....... "I'm at bank"
     ....... "widthdrew $100 from personal"
     ....... "leaving bank"
     . then buys food from market
     ... Messages:
     ....... "I'm going to market"
     ....... "I'm at Market"
     ....... "Got food from market"
     . then goes home with restocked food
     ....... "I'm going home to eat"
     ....... "I'm Eatin at Home"
     ....... "I have food now"
     ....... "I have a selection of: "
     ....... "I'm hungry and want to make: "
     ....... "Cooking food"
     ....... "food is done"
     4. Person lives in home, cooks for himself, then goes to bank & market to restock food 
     . follow "poorHome05 low steak"
     . lives in the fourth home from top right
     . eats at home
     ... Messages:
     ....... "I'm going home to eat"
     ....... "I'm Eatin at Home"
     ....... "I'm hungry and want to make: "
     ....... "cooking food"
     ....... "food is done!!!"
     . goes to bank first to get money
     ... Messages:
     ....... "I'm going to bank"
     ....... "I'm at bank"
     ....... "widthdrew $100 from personal"
     ....... "leaving bank"
     . then buys food from market
     ... Messages:
     ....... "I'm going to market"
     ....... "I'm at Market"
     ....... "Got food from market"
     . then goes home with restocked food
     ....... "I have food now"
     5. Day worker, takes the bus to work
      . follow "Cook01DayPoor"
      . lives in the apartment on the second row, first from left
      . takes the bus to work
      . works at restaurant, first from top left
      ... Messages:
      ....... "I'm going to work"
      ....... "I'm going to bus stop"
      ....... "getting on bus"
      ....... "getting off bus"
      ....... "I'm at work"
     6. Day worker, walks to work
      . lives in apartment on first row, 1st from left
      . walks to work
      . works at first restaurant from top left
      ... Messages:
      ....... "I'm going to work"
      ....... "I'm at work"
      
     
     
	
###Participation from each Member
  + Chad Martin: CardLayout, Person Agent, Person Agent tests, City Animation Panel, Add tables, Gui Interaction Panel, photoshopping of images, interaction diagrams
  + Gabriel Lew: Bank Agent, Bank Customer Role, Bank tests, Bank Animation, Manager employees payment behavior in Person Agent, landlord renter in person agent, general structure of wiki and images
  + Gerson Hernandez: Bus Agent, Bus Gui, bus tests, apartment animation layout
  + Emily Bernstein: Market Agent, Market Customer Role, Clerk Role, Delivery Man Role, Delivery Man and Clerk Gui, Delivery Man Driving Gui, Market Panel, Market tests(including market roles)
  + Garland Chen: AtHome role, AtHome tests, RepairMan role, Apartment Gui && animation upgrades, AtHome gui, Home, Residence
