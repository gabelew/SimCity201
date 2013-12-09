package GLRestaurant.gui;
import GLRestaurant.gui.GLHostGui.Command;
import GLRestaurant.roles.GLCookRole;
import city.gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GLCookGui implements Gui {
    private BufferedImage cookImg = null;
	private final int START_POSITION = -20;
	private final int xWorkingPosition = 525;
	private final int yWorkingPosition = 160;
	private final int NGRILLS = 3;
	private final int NPLATES = 3;
	private static final int GRILLY = 140;
	private static final int GRILLONEX = 565;
	private static final int GRILLTWOX = 585;
	private static final int GRILLTHREEX = 510;
	
	private static final int PLATEONEX = 514;
	private static final int PLATEONEY = 212;
	
	private static final int PLATETWOX = 531;
	private static final int PLATETWOY = 220;
	
	private static final int PLATETHREEX = 540;
	private static final int PLATETHREEY = 226;
	
	private boolean isPresent = false;
	enum Command {none, enterRestaurant, leaveRestaurant};
	Command command = Command.none;
	
	private class Grill {
		int grillNum;
		int orderNum;
		boolean occupied = false;
		String item;
		Grill(int grillNum) {
			this.grillNum = grillNum;
		}
	}
	
	private class Plate {
		int plateNum;
		int orderNum;
		boolean occupied = false;
		String item;
		Plate(int plateNum) {
			this.plateNum = plateNum;
		}
	}
	
    private GLCookRole role = null;
    private List<Grill> grills;
    private List<Plate> plates;
    private int xPos = START_POSITION, yPos = START_POSITION;//default position
    private int xDestination = START_POSITION, yDestination = START_POSITION;//default start position

    public GLCookGui(GLCookRole agent) {
    	try {
		    cookImg = ImageIO.read(new File("imgs/chef_v1.png"));
		} catch (IOException e) {
		}
        this.role = agent;
        grills = new ArrayList<Grill>(NGRILLS);
        for(int i = 1; i <= NGRILLS; i++) {
        	grills.add(new Grill(i));
        }
        plates = new ArrayList<Plate>(NPLATES);
        for(int ix = 1; ix <= NPLATES; ix++) {
        	plates.add(new Plate(ix));
        }
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
        if(xPos == xDestination && yPos == yDestination){
        	if(command == Command.leaveRestaurant){
        		role.msgAnimationHasLeftRestaurant();
        	}else if(command == Command.enterRestaurant){
        	}
        	command = Command.none;	
        }
    }

    public void draw(Graphics2D g) {
    	g.drawImage(cookImg, xPos, yPos, null);
        g.setColor(Color.white);
        if(grills.get(0).occupied) {
        	g.drawString(grills.get(0).item, GRILLONEX, GRILLY);
        }
        if(grills.get(1).occupied) {
        	g.drawString(grills.get(1).item, GRILLTWOX, GRILLY);
        }
        if(grills.get(2).occupied) {
        	g.drawString(grills.get(2).item, GRILLTHREEX, GRILLY);
        }
        
        g.setColor(Color.black);
        if(plates.get(0).occupied) {
        	g.drawString(plates.get(0).item, PLATEONEX, PLATEONEY);
        }
        if(plates.get(1).occupied) {
        	g.drawString(plates.get(1).item, PLATETWOX, PLATETWOY);
        }
        if(plates.get(2).occupied) {
        	g.drawString(plates.get(2).item, PLATETHREEX, PLATETHREEY);
        }
    }
    
    public void cook(String item, int orderNum) {
    	for(Grill g: grills) {
    		if(!g.occupied) {
    			g.occupied = true;
    			if("steak".equals(item)) {
    				g.item = "ST";
    			}
    			else if("chicken".equals(item)) {
    				g.item = "CH";
    			}
    			else if("salad".equals(item)) {
    				g.item = "SD";
    			}
    			else if("cookie".equals(item)){
    				g.item = "CK";
    			}
    			g.orderNum = orderNum;
    			break;
    		}
    	}
    }
    
    public void finCook(int orderNum) {
    	for(Grill g: grills) {
    		if(orderNum == g.orderNum) {
    			g.occupied = false;
    			g.item = "";
    			g.orderNum = -1;
    			break;
    		}
    	}
    }
    
    public void DoLeaveRestaurant() {
        xDestination = START_POSITION;
        yDestination = START_POSITION;
        command = Command.leaveRestaurant;
    }
    public void DoEnterRestaurant(){
        xDestination = xWorkingPosition;
        yDestination = yWorkingPosition; 
        command = Command.enterRestaurant;   	
    }
    
    public void plate(String item, int orderNum) {
    	for(Plate p: plates) {
    		System.out.println("Plating " + item + " " + p.plateNum + " is occupied?: " + p.occupied);
    		if(p.occupied) {
    			System.out.println("Platenum " + p.plateNum + " is occupied with: " + p.item);
    		}
    		if(!p.occupied) {
    			p.occupied = true;
    			if("steak".equals(item)) {
    				p.item = "ST";
    			}
    			else if("chicken".equals(item)) {
    				p.item = "CH";
    			}
    			else if("salad".equals(item)) {
    				p.item = "SD";
    			}
    			else if("cookie".equals(item)){
    				p.item = "CK";
    			}
    			p.orderNum = orderNum;
    			break;
    		}
    	}
    }
    
    public int getPlateX(int orderNum) {
    	int numPlate = -1;
    	for(Plate p : plates) {
    		if (p.orderNum == orderNum) {
    			numPlate = p.plateNum;
    		}
    	}
    	if(1 == numPlate)
    		return PLATEONEX;
    	else if (2 == numPlate)
    		return PLATETWOX;
    	else if (3 == numPlate)
    		return PLATETHREEX;
    	else return xWorkingPosition;
    }
    
    public int getPlateY(int orderNum) {
    	int numPlate = -1;
    	for(Plate p : plates) {
    		if (p.orderNum == orderNum) {
    			numPlate = p.plateNum;
    		}
    	}
    	if(1 == numPlate)
    		return PLATEONEY;
    	else if (2 == numPlate)
    		return PLATETWOY;
    	else if (3 == numPlate)
    		return PLATETHREEY;
    	else return yWorkingPosition;
    }
    
    public void finPlate(int orderNum) {
    	for(Plate p: plates) {
    		if(orderNum == p.orderNum) {
    			p.occupied = false;
    			p.item = "";
    			p.orderNum = -1;
    			break;
    		}
    	}
    }

    public boolean isPresent() {
        return isPresent;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	@Override
	public void setPresent(boolean b) {
		isPresent = b;
	}
}
