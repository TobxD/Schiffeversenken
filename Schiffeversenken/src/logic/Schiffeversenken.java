package logic;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import connection.*;
import graphics.*;

/**
 * Hauptklasse des Spiels.
 */

public class Schiffeversenken {
	
	private EigeneMap eigeneMap;
	private GegnerischeMap gegnMap;
	private Controller contr;
	private Peer peer;
	private boolean amZug;
	
	public static void main(String[]args) {
		new Schiffeversenken(new Controller());
	}
	
	public Schiffeversenken(Controller contr) {
		this.contr = contr;
		contr.setSchiffeversenken(this);
		peer = null;
		
		contr.zeigeIPAdresse(Peer.getIP());	// IP-Adresse wird unten in GUI angezeigt
		contr.askForPeer(Peer.getIP());		// Benutzer wird gefragt, ob er neues Spiel eroeffnen will oder Spiel beitreten will
											// Je nach Eingabe wird ein Server- oder Client-Peer erzeugt
		eigeneMap = new EigeneMap();
		eigeneMap.schiffeSetzenAuto();		// Schiffe werden zufällig gesetzt
		gegnMap = new GegnerischeMap();
		eigeneMap.paint(contr);	// Maps werden gezeichnet
		gegnMap.paint(contr);
	}
	
	/**
	 * Diese Methode wird von den Peer-Unterklassen aufgerufen, wenn die Verbindung erfolgreich 
	 * aufgebaut wurde. 
	 */
	public void verbindungHergestellt() {
		Thread thread = new Thread() {
			@Override public void run() {
				contr.zeigeVerbindungsstatus("Verbunden");
         		contr.zeigeSpieldauer();
         		contr.buttonsSchalten(amZug);
        		if(amZug) {
        			zugAusfuehren();
        		} else {
        			warten();
        		}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	private void aufVerbindungWarten() {
		Thread thread = new Thread() {
			@Override public void run() {
            	contr.zeigeVerbindungsstatus("Wird hergestellt...");
             }
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	public void verbindungHerstellen(String ipString) {
		try {
			if(ipString == null) {
				amZug = true;
				peer = new Server(this);
				Thread thread = new Thread() {
					@Override
					public void run() {
						aufVerbindungWarten();
						((Server) peer).connect();
					}
				};
				thread.start();
			} else {
				amZug = false;
				peer = new Client(this, ipString);
				Thread thread = new Thread() {
					@Override
					public void run() {
						aufVerbindungWarten();
						((Client) peer).connect();
					}
				};
				thread.start();
			}
		} catch (UnknownHostException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void zugAusfuehren() {
		amZug = true;
		contr.buttonsSchalten(amZug);
		contr.setZugstatus("Sie sind am Zug");
	}
	
	/**
	 * Diese Methode wird aufgerufen, wenn der Spieler einen Schuss abgegeben hat. Für die Anweisungen
	 * wird ein Thread eröffnet, damit das Spiel beim Warten auf die Antwort des Gegners nicht einfriert.
	 * Anweisungen, die graphische Objekte verändern, werden in einem extra Swing-Thread aufgerufen.
	 * <p>
	 * Je nachdem, ob der Schuss erfolgreich war oder nicht, werden sowohl die GUI als auch die
	 * Daten aktualisiert.
	 */
	public void beschiessen(final int x, final int y) {
		if(!gegnMap.wurdeBeschossen(x, y)) { 	// wenn das Feld bereits noch nicht beschossen wurde,
			Thread thread = new Thread() {		// wird der Anweisungsblock der if-Schleife durchlaufen
				@Override public void run() {
					try {
						peer.sendReq(new RequestProtocol(x,y));
					} catch (IOException e) {
						e.printStackTrace();
					}
					ResponseProtocol res = peer.readRes();
					if(res.isGetroffen()) {
						gegnMap.eintragen(x, y, true);	// Gegnerische Map: Matrix aktualisieren (Treffer)
						gegnMap.paint(contr);			// Gegnerische Map: grafisch aktualisieren
						if(gegnMap.alleVersenkt()) {
							spielendeAnzeigen(true);
						}
						zugAusfuehren(); // erneut Zug ausführen
					} else {
						gegnMap.eintragen(x, y, false);	// Gegnerische Map: Matrix aktualisieren (Niete)
						gegnMap.paint(contr);			// Gegnerische Map: grafisch aktualisieren
						if(gegnMap.alleVersenkt()) {
							spielendeAnzeigen(true);
						}
						warten();	// auf Gegner warten
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}
	
	/**
	 * Diese Methode wird aufgerufen, wenn der Gegner gerade am Zug ist. Für die Anweisungen
	 * wird ein Thread eröffnet, damit das Spiel beim Warten auf die Antwort des Gegners nicht einfriert.
	 * Anweisungen, die graphische Objekte verändern, werden in einem extra Swing-Thread aufgerufen.
	 * <p>
	 * Je nachdem, ob der Schuss des Gegners erfolgreich war, wird weiter gewartet oder man ist selbst
	 * am Zug.
	 */
	public void warten() {
		Thread thread = new Thread() {
			@Override public void run() {
				amZug = false;
				SwingUtilities.invokeLater(new Runnable() {
					@Override 
					public void run() {
						contr.buttonsSchalten(amZug);					// GUI wird aktualisiert
						contr.setZugstatus("Der Gegner ist am Zug");
					}
				});
				RequestProtocol req = peer.readReq();	// Protokoll wird gelesen und ausgewertet
				boolean istGetroffen = eigeneMap.istGetroffen(req.getX(), req.getY());
				try {
					peer.sendRes(new ResponseProtocol(istGetroffen));	// Antwort wird gesendet
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(istGetroffen) {
					eigeneMap.paint(contr);	// GUI: Eigene Map wird neu gezeichnet
					if(eigeneMap.alleVersenkt()) {
						spielendeAnzeigen(false);
	               	 }
					 warten();
				} else {
					amZug = true;
					eigeneMap.paint(contr);
					if(eigeneMap.alleVersenkt()) {
						spielendeAnzeigen(false);
	               	}
	               	zugAusfuehren();
					
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	private void spielendeAnzeigen(final boolean gewonnen) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override 
			public void run() {
				contr.spielendeAlert(gewonnen);
			}
		});
	}
	
	public boolean proofIP(String ipString) {
		return Peer.proofIP(ipString);
	}
	
	public void verbindungUnterbrochen() {
		// TODO
	}
}
