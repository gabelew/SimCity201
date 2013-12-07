package EBRestaurant.gui;

import javax.swing.*;

import city.gui.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class EBAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 450;
    private final int WINDOWY = 350;
    private final int RectX=100;
    private final int Rect1Y=100;
    private final int Rect2Y=200;
    private final int Rect3Y=300;
    private final int RectWH=45;
    private final int RectPos=0;
    private final int time=15;
    private Dimension bufferSize;
    private List<Gui> guis = new ArrayList<Gui>();

    public EBAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(time, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(RectPos, RectPos, WINDOWX, WINDOWY );
        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(RectX, Rect1Y, RectWH, RectWH);
        g2.fillRect(RectX, Rect2Y, RectWH, RectWH);
        g2.fillRect(RectX, Rect3Y, RectWH, RectWH);
        
        g2.fillRect(300, 230, 20, 100);
        g2.fillRect(300, 200, 80, 20);

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(EBCustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(EBHostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(EBWaiterGui gui){
    	guis.add(gui);
    }
    
    public void addGui(EBCookGui gui){
    	guis.add(gui);
    }

    
}
