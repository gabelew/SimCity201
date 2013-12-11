package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import city.PersonAgent;





import javax.imageio.ImageIO;

import city.BusAgent;
import city.gui.AnimationPanel.GridSpot;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class BusGui implements Gui{
	
	private BusAgent agent = null;
	private boolean isPresent = false;
	private char type;

	
	private static BufferedImage bus_back = null;
	private static BufferedImage bus_front = null;
	public SimCityGui gui;
	public int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, atBusStop};
	private Command command=Command.noCommand;

	static final int B_YPOS_BEGIN = -35;
	static final int F_YPOS_BEGIN = -405;
    static final int B_XStart = 30;
    static final int B_YStart = 410;
    static final int F_XStart = 825;
    static final int F_YStart = -40;
    static final int BusStop0 = 305;
    static final int BusStop1 = 225;
    static final int BusStop2 = 145;
    static final int BusStop3 = 65;


	
	public BusGui(BusAgent b, SimCityGui gui, char type){ 
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    bus_back = ImageIO.read(new File(path.toString() + "bus_back.png"));
		    bus_front = ImageIO.read(new File(path.toString() + "bus_front.png"));
		} catch (IOException e) {
		}
		
		agent = b;
		this.type = type;
		
		if(type == 'B'){
			xPos = B_XStart;
			yPos = B_YPOS_BEGIN;
		}
		else if(type == 'F'){
			xPos = F_XStart;
			yPos = F_YPOS_BEGIN;
		}
        
		this.gui = gui;
		
	}

	
	@Override
	public void updatePosition() {
		boolean canKeepMoving = true;

		Point point = new Point(817,115);
			if(type == 'F' && yPos>=115-35 && yPos<=115+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 796
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 840){
					((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}

			if(PersonAgent.randInt(0, 480)!=0 && type == 'F' && yPos==115+80*0-37){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}
			if(type == 'F' && yPos==115+80*0+22){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			
			point = new Point(817,115+80*1);
			if(type == 'F' && yPos>=115+80*1-35 && yPos<=115+80*1+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 796
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 840){
					((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'F' && yPos==115+80*1-37){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}
			if(type == 'F' && yPos==115+80*1+22){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			
			
			point = new Point(817,115+80*2);
			if(type == 'F' && yPos>=115+80*2-35 && yPos<=115+80*1+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 796
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 840){
				((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'F' && yPos==115+80*2-37){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}
			if(type == 'F' && yPos==115+80*2+22){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			
			point = new Point(817,115+80*3);
			if(type == 'F' && yPos>=115+80*3-35 && yPos<=115+80*1+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 796
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 840){
				((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'F' && yPos==115+80*3-37){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}
			if(type == 'F' && yPos==115+80*3+22){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			
			point = new Point(17,115+80*0);
			if(type == 'B' && yPos>=115-35 && yPos<=115+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 5
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 44){
					((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(type == 'B' && yPos==115+80*0-37){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'B' && yPos==115+80*0+22){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}
			
			point = new Point(17,115+80*1);
			if(type == 'B' && yPos>=115+80*1-35 && yPos<=115+80*1+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 5
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 44){
					((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(type == 'B' && yPos==115+80*1-37){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'B' && yPos==115+80*1+22){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}

			
			point = new Point(17,115+80*2);
			if(type == 'B' && yPos>=115+80*2-35 && yPos<=115+80*2+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 5
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 44){
					((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(type == 'B' && yPos==115+80*2-37){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'B' && yPos==115+80*2+22){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}
			
			point = new Point(17,115+80*3);
			if(type == 'B' && yPos>=115+80*3-35 && yPos<=115+80*3+20 
					&& gui.animationPanel.gridMap.get(point).owner!=null
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos >= 5
					&& ((PersonAgent) gui.animationPanel.gridMap.get(point).owner).getGui().xPos <= 44){
					((PersonAgent) gui.animationPanel.gridMap.get(point).owner).msgYourCrashedPayInsurance();
			}
			if(type == 'B' && yPos==115+80*3-37){
				if(gui.animationPanel.gridMap.get(point).spot.availablePermits()==0){
					gui.animationPanel.gridMap.get(point).spot.release();
				}
			}
			if(PersonAgent.randInt(0, 480)!=0 && type == 'B' && yPos==115+80*3+22){
				canKeepMoving = gui.animationPanel.gridMap.get(point).spot.tryAcquire();
			}

			
			if(canKeepMoving)
			if (yPos != yDestination && type == 'F' && yPos < 420){
				yPos++;
			}else if (yPos != yDestination && type == 'B' && yPos > -50){
				yPos--;
			}else if(yPos != yDestination && type == 'B' && yPos <= -40){
				yPos = B_YStart;
			}else if(yPos != yDestination && type == 'F' && yPos >= 410){
				yPos = F_YStart;
			}

				if (xPos == xDestination && yPos == yDestination) {
					if(command == Command.atBusStop){
						command = Command.noCommand;
						agent.msgAtStop(new Point(xDestination, yDestination));
					}
				}

	}

	@Override
	public void draw(Graphics2D g) {
		if(xPos == B_XStart)
		{
			g.drawImage(bus_back, xPos, yPos, null);
		}
		if(xPos == F_XStart){
			g.drawImage(bus_front, xPos, yPos, null);
		}

		
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void GoToNextBusStop(){
		
		if(type == 'B'){yDestination = NextBY();}
		else if(type == 'F'){yDestination = NextFY();}
		command = Command.atBusStop;
	}
	
	
	public int NextBY(){
		int y = F_YStart;			
			if(yPos>BusStop0){
				y=BusStop0;
			}else if(yPos>BusStop1){
				y=BusStop1;
			}else if(yPos>BusStop2){
				y=BusStop2;
			}else if(yPos>BusStop3){
				y=BusStop3;
			}else{
				y=BusStop0;
			}
	
			return y;
	}
	
	public int NextFY(){
		int y = B_YStart;
			if(yPos<BusStop3){
				y=BusStop3;
			}else if(yPos<BusStop2){
				y=BusStop2;
			}else if (yPos<BusStop1){
				y=BusStop1;
			}else if(yPos<BusStop0){
				y=BusStop0;
			}else{
				y=BusStop3;
			}
		
		return y;
	}
	
	public void doGoToRest() {
		if(type == 'B'){
			xDestination = B_XStart;
			yDestination = B_YStart;
		}
		
		else if(type == 'F'){
			xDestination = F_XStart;
			yDestination = F_YStart;
		}
	}

}
