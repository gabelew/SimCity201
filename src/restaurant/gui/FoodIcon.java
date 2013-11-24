package restaurant.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FoodIcon {

	public BufferedImage iconImg = null;
	public String type;

	public FoodIcon(String t)
	{
		try {
				StringBuilder path = new StringBuilder("imgs/" + t.toLowerCase()+".png");
			    iconImg = ImageIO.read(new File(path.toString()));
			} catch (IOException e) {
			}
		if(t.contains("q"))
			type = "question";
		else if(t.contains("g"))
			type = "grill";
		else 
			type = "food";
	}
}
