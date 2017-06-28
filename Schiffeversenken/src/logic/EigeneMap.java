package logic;

import graphics.Controller;
import java.util.*;
public class EigeneMap extends Spielfeld {
	
	public EigeneMap() {
		super();
	}
	
	public boolean istGetroffen(int x, int y) {
		if(matrix[x][y] == Feld.SCHIFF) {
			matrix[x][y] = Feld.TREFFER;
			treffer++;
			return true;
		} else {
			matrix[x][y] = Feld.NIETE;
			return false;
		}
	}
	
	public void schiffeSetzenAuto() {
		// 5er Schiff
		matrix[0][0] = Feld.SCHIFF;
		matrix[0][1] = Feld.SCHIFF;
		matrix[0][2] = Feld.SCHIFF;
		matrix[0][3] = Feld.SCHIFF;
		matrix[0][4] = Feld.SCHIFF;
		// 4er Schiff
		matrix[3][2] = Feld.SCHIFF;
		matrix[4][2] = Feld.SCHIFF;
		matrix[5][2] = Feld.SCHIFF;
		matrix[6][2] = Feld.SCHIFF;
		// 3er Schiff
		matrix[7][8] = Feld.SCHIFF;
		matrix[8][8] = Feld.SCHIFF;
		matrix[9][8] = Feld.SCHIFF;
		// 2er Schiff
		matrix[4][4] = Feld.SCHIFF;
		matrix[4][5] = Feld.SCHIFF;
		// 1er Schiff
		matrix[8][0] = Feld.SCHIFF;/*
		
		for(int i = 5; i > 0; i--) { // Schleife, die fuer jedes Schiff von 5 bis 1 jeweils einmal durchlaufen wird
			int x = new Random().nextInt(10);
			int y = new Random().nextInt(10);
			boolean b = false;
			while(b) {
				if(matrix[x][y] == Feld.SCHIFF) {		// es wird geprueft, ob an der Stelle mit den zufaelligen Koordinaten
					x = new Random().nextInt(10);		// bereits ein Schiff ist.  
					y = new Random().nextInt(10);		// 
				} else if(10 - x < i || 10 - y < i) {	// Nur wenn entweder 10 - x kleiner i oder
					matrix[x][y] = Feld.SCHIFF;			// 10 - y kleiner i ist
					b = true;
				}
			// Jetzt hat man einen Schiffsteil an einem beliebigen Punkt
			
			}
		}*/
	}
	public void SchiffSetzen( int Laenge){

		int x = new Random().nextInt(10);
		int y = new Random().nextInt(10);
		
		for(int x1 =-1; x1<=1; x1++)
		{
			for(int y1 =-1; y1<=1;y1++)
			{
				if(matrix[x1][y1] == Feld.SCHIFF)
				{
					SchiffSetzen(Laenge);
				}
			}
		}
		
		boolean geht = true;
		
		for(int x1 = x-Laenge; x1<=x+Laenge; x1++ )
		{
			for(int y1=-1; y1<=1;y1++)
			{
				if ( matrix[x1][y1]== Feld.SCHIFF)
				{
					geht = false;
				}
			}
		}
		
		int b=0;
		for (int x1 = x-Laenge; x1<=x+Laenge; x1++ )
		{
				if (geht ==true)
				{
					b=b+1;
				}
				else
				{
					b=0;
				}
				
				if (b==Laenge)
				{
					for(int x11 = 0; x11<=Laenge; x11++ ) 
					{
						matrix[x11][y]=Feld.SCHIFF;
					}
					return;
				}
		}
		
		for(int x1=-1; x1<=1; x1++)
		{
			for(int y1 = y-Laenge; y1<=y+Laenge ;y1++)
			{
				if ( matrix[x1][y1]== Feld.SCHIFF)
				{
					geht = false;
				}
			}
		}
	}
	
	
	public void paint(Controller contr) {
		for(int y = 0; y < 10; y++) {
			for(int x = 0; x < 10; x++) {
				contr.paintEigeneMap(matrix[x][y].getImage(), x, y);
			}
		}
	}
}
