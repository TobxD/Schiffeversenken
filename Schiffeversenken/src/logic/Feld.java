package logic;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Feld {
	
	SCHIFF("resources/schiff.png"), TREFFER("resources/treffer.png"), NIETE("resources/niete.png"), WASSER("resources/wasser.png");

		   private Image image;

		   Feld(String path) {
				try {
					this.image = ImageIO.read(new File(path));
				} catch (IOException e) {
					System.err.println(new File(path).getAbsolutePath());
					e.printStackTrace();
				} 
		   }
		   
		   public Image getImage() {
			   return image;
		   }
}
