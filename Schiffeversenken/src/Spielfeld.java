public class Spielfeld {
	Zustand[][] feld;
	
	public Spielfeld(){
		feld = new Zustand[10][10];
	}
	
	public boolean pruefe(int y, int x){
		switch(feld[y][x]){
		case WASSER:
			feld[y][x] = Zustand.NIETE;
			return false;
		case SCHIFF:
			feld[y][x] = Zustand.TREFFER;
			return true;
		default:
			System.err.println("fail! pruefe schon beschossenes Feld");
			return false;
		}
	}
}