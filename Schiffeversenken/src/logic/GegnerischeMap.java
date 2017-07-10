package logic;

import graphics.Controller;

public class GegnerischeMap extends Spielfeld {
	
	public GegnerischeMap() {
		super();
	}
	
	public void eintragen(int x, int y, boolean getroffen) {
		if(getroffen) {
			matrix[y][x] = Feld.TREFFER;
			treffer++;
		} else {
			matrix[y][x] = Feld.NIETE;
		}
	}
	
	public void paint(Controller contr) {
		for(int y = 0; y < 10; y++) {
			for(int x = 0; x < 10; x++) {
				contr.paintGegnMap(matrix[y][x].getImage(), x, y);
			}
		}
	}
	
	public boolean wurdeBeschossen(int x, int y) {
		if(matrix[y][x] == Feld.TREFFER) {
			return true;
		} else if(matrix[y][x] == Feld.NIETE) {
			return true;
		} else {
			return false;
		}
	}
}
