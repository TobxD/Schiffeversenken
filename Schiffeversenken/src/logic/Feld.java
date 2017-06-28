package logic;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Feld {
	
	SCHIFF("schiff.png"), TREFFER("treffer.png"), NIETE("niete.png"), WASSER("wasser.png");
	//SCHIFF("."), TREFFER("."), NIETE("."), WASSER(".");

		   private Image image;

		   Feld(String path) {
				try {
					/*for(String s : new File(path).list()){
						System.out.println(s);
					}*/
					this.image = ImageIO.read(new File(path));
				} catch (IOException e) {
					System.out.println(new File(path).getAbsolutePath());
					//System.out.println(new File(path).exists());
					e.printStackTrace();
				} 
		   }
		   
		   public Image getImage() {
			   return image;
		   }
}
