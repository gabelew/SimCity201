package bank.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import city.animationPanels.BankAnimationPanel;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.BankCustomerRole;
import city.roles.BankRobberRole;

public class BankRobberGui implements Gui{

	private BankRobberRole role = null;
	private boolean isPresent = false;
	private static BufferedImage robberImg = null;
	
	private int xPos = CUST_START_POS, yPos = CUST_START_POS;
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	
	private enum Command {noCommand, WaitForATM, GoToATM, LeaveBank};
	private Command command = Command.noCommand;
	private int atmNumber = -1;
	List<Integer>atmsVisited = new ArrayList<Integer>();
	private Map<Integer, Point> atmMap = new HashMap<Integer, Point>();
	
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 50;
	static final int yWAITING_START = 70;
	static final int NATMS = 16;
	static final int NATMS_ROWS = 4;
	static final int NATMS_COLUMNS = 4;
	static final int ATM_X_START = 200;
	static final int ATM_Y_START = 40;
	static final int ATM_X_GAP = 100;
	static final int ATM_Y_GAP = 90;
	
	public BankRobberGui(BankRobberRole role) {
		try {
			StringBuilder path = new StringBuilder("imgs/");
			robberImg = ImageIO.read(new File(path.toString() + "robber.png"));
		} catch(IOException e) {
			
		}
		this.role = role;
		
		for(int i = 0; i < NATMS/NATMS_ROWS; i++) {
			for(int j = 0; j < NATMS/NATMS_COLUMNS; j++) {
				atmMap.put(j+i*4, new Point(i*ATM_X_GAP+ATM_X_START, j*ATM_Y_GAP+ATM_Y_START));
			}
		}
		
	}
	
	@Override
	public void updatePosition() {
		if (xPos < xDestination) 
			xPos++;
		else if (xPos > xDestination)
			xPos --;
		
		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		if (xPos == xDestination && yPos == yDestination) {
			if(command == Command.WaitForATM && xDestination == xWAITING_START && yDestination == yWAITING_START) {
				role.msgAnimationFinishedEnterBank();
			} else if (command == Command.GoToATM) {
				role.msgAtATM();
			} else if (command == Command.LeaveBank) {
				role.msgLeftBank();
			}
			command = Command.noCommand;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(robberImg, xPos, yPos, null);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void DoEnterBank() {
		xDestination = xWAITING_START;
		yDestination = yWAITING_START;
		command = Command.WaitForATM;
	}
	
	public void DoLeaveBank() {
		xDestination = CUST_START_POS;
		yDestination = CUST_START_POS;
		for(int i : atmsVisited) {
			((BankAnimationPanel)role.bank.insideAnimationPanel).atms.get(i).release();
		}
		atmNumber = -1;
		atmsVisited.removeAll(atmsVisited);
		command = Command.LeaveBank;
	}
	
	public void DoGoToATM() {
		findATM();
		if(atmNumber >= 0) {
			command = Command.GoToATM;
		} else {
			role.msgNoMoreATMS();
		}
		atmNumber = -1;
	}
	
	private void findATM() {
		for(int i = 0; i < ((BankAnimationPanel)role.bank.insideAnimationPanel).atms.size(); i++){
			if(atmNumber < 0) {
					if(((BankAnimationPanel)role.bank.insideAnimationPanel).atms.get(i).tryAcquire()) {
						atmNumber = i;
						atmsVisited.add(i);
						xDestination = atmMap.get(i).x - 28;
						yDestination = atmMap.get(i).y + 4;
					}
			}
		}
	}

	public void setPresent(boolean b) {
		isPresent = b;
		
	}

}
