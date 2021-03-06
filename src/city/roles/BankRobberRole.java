package city.roles;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import bank.BankBuilding;
import bank.gui.BankRobberGui;

public class BankRobberRole extends Role{
	public BankBuilding bank;
	public enum GuiState {None, EnteringBank, InBank, FindingATM, AtAtm, LeavingBank, DoneRobbing, Robbing};
	public GuiState state = GuiState.None;
	private BankRobberGui robberGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	static final int ALGORITHM_MIN = 1, ALGORITHM_MAX = 4;
	static final int AT_ATM_TIME = 1200;
	Timer timer = new Timer();

	
	public BankRobberRole(PersonAgent p) {
		super(p);
	}
	
	// Messages
	public void goingToBank() {
		state = GuiState.EnteringBank;
		stateChanged();
	}
	
	public void msgHackSuccessful(double amountStolen){
    	AlertLog.getInstance().logMessage(AlertTag.PERSON, this.getName(), "Hack succeeded! Stole $" + amountStolen);
		state = GuiState.DoneRobbing;
		myPerson.cashOnHand += amountStolen;
		stateChanged();
	}
	
	public void msgHackDefended() {
    	AlertLog.getInstance().logMessage(AlertTag.PERSON, this.getName(), "Hack failed against bank's defense algorithms. Trying next ATM.");
		state = GuiState.InBank;
		stateChanged();
	}
	
	// msgs from animation
	public void msgAtATM() {
		waitingResponse.release();
		state = GuiState.AtAtm;
		stateChanged();
	}
	
	public void msgNoMoreATMS() {
    	AlertLog.getInstance().logMessage(AlertTag.PERSON, this.getName(), "No more ATMS to rob. Running away from bank.");
		waitingResponse.release();
		state = GuiState.LeavingBank;
		stateChanged();
	}
	
	public void msgAnimationFinishedEnterBank() {
		waitingResponse.release();
		stateChanged();
	}
	
	public void msgLeftBank() {
		waitingResponse.release();
		stateChanged();
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		if(GuiState.EnteringBank.equals(state)) {
			state = GuiState.InBank;
			EnterBank();
			return true;
		} else if(GuiState.InBank.equals(state)) {
			state = GuiState.FindingATM;
			FindATM();
			return true;
		} else if(GuiState.AtAtm.equals(state)) {
			state = GuiState.Robbing;
			RobBank();
			return true;
		
		} else if(GuiState.LeavingBank.equals(state)) {
			state = GuiState.None;
			LeaveBank();
			return true;
		}
		
		if(GuiState.DoneRobbing.equals(state)) {
			state = GuiState.LeavingBank;
			return true;
		}
		return false;
	}

	private void EnterBank() {
		robberGui.DoEnterBank();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void FindATM() {
		robberGui.DoGoToATM();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static int randInt(int min, int max) {
	    Random i = new Random();
	    return i.nextInt((max - min) + 1) + min;
	}
	
	private void RobBank() {
		final BankRobberRole r = this;
		timer.schedule(new TimerTask() {
			public void run() {
		    	AlertLog.getInstance().logMessage(AlertTag.PERSON, r.getName(), "Attempting robbery");
				int hackAlgorithm = randInt(ALGORITHM_MIN,ALGORITHM_MAX);
				myPerson.bankTeller.msgThisIsAHackAttack(r, hackAlgorithm);
			}
		}, AT_ATM_TIME);
	}
	
	private void LeaveBank() {		
		AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "Leaving bank.");
		robberGui.DoLeaveBank();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		myPerson.msgDoneAtBank();
	}
	
	@Override
	public void setGui(Gui g) {
		this.robberGui = (BankRobberGui) g;
	}

	@Override
	public Gui getGui() {
		return this.robberGui;
	}
	
	public void setBankBuilding(BankBuilding b) {
		this.bank = b;
	}
	
	public PersonAgent getPersonAgent() {
		return myPerson;
	}
}
