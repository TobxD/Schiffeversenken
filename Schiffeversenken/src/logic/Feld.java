package logic;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Feld {
	
	SCHIFF("logic/schiff.png"), TREFFER("logic/treffer.png"), NIETE("logic/niete.png"), WASSER("logic/wasser.png");

		   private Image image;

		   Feld(String path) {
				try {
					this.image = ImageIO.read(new File(path));
				} catch (IOException e) {
					e.printStackTrace();
				} 
		   }
		   
		   public Image getImage() {
			   return image;
		   }
}
