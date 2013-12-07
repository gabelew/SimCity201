package bank;

import java.awt.Point;
import city.animationPanels.InsideAnimationPanel;

public class BankBuilding{

	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	private boolean isOpen = true;
	
	public BankBuilding(InsideAnimationPanel iap, Point loc)
	{
		this.insideAnimationPanel = iap;
		this.location = loc;
	}

	public BankBuilding(Point point) {
		this.location = point;
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	public void setIsOpen(boolean b){
		this.isOpen = b;
	}
}
