package logic;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import connection.*;
import graphics.*;

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
		eigeneMap.schiffeSetzenAuto();
		gegnMap = new GegnerischeMap();
		eigeneMap.paint(contr);	// Maps werden gezeichnet
		gegnMap.paint(contr);
	}
	
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
        		this.interrupt();
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
						this.interrupt();
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
						this.interrupt();
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
	
	public void beschiessen(final int x, final int y) {
		Thread thread = new Thread() {
			@Override public void run() {
				try {
					peer.sendReq(new RequestProtocol(x,y));
				} catch (IOException e) {
					// Verbindung unterbrochen, Hinweis auf GUI, erneuter Verbindungsaufbau
					e.printStackTrace();
				}
				ResponseProtocol res = peer.readRes();
				if(res.isGetroffen()) {
					gegnMap.eintragen(x, y, true);	// Gegnerische Map Matrix aktualisieren
					gegnMap.paint(contr);			// Gegnerische Map grafisch aktualisieren
					SwingUtilities.invokeLater(new Runnable() {
						@Override 
						public void run() {
							if(gegnMap.alleVersenkt()) {
								System.out.println("Alle versenkt");
								contr.spielendeAlert(true);
								// Spiel zuruecksetzen
								// evtl. neuer Verbindungsaufbau
							}
							zugAusfuehren();
						}
					});					// GUI anzeigen, dass nochmal geschossen werden darf
				} else {
					gegnMap.eintragen(x, y, false);	// gegnMap aktualisieren (Niete)
					gegnMap.paint(contr);			// Gegnerische Map grafisch aktualisieren
					SwingUtilities.invokeLater(new Runnable() {
						@Override 
						public void run() {
							if(gegnMap.alleVersenkt()) {
								System.out.println("Alle versenkt");
								contr.spielendeAlert(true);
								// Spiel zuruecksetzen
								// evtl. neuer Verbindungsaufbau
							}
							warten();
						}
					});
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	public void warten() {
		Thread thread = new Thread() {
			@Override public void run() {
				amZug = false;
				SwingUtilities.invokeLater(new Runnable() {
					@Override 
					public void run() {
						contr.buttonsSchalten(amZug);
						contr.setZugstatus("Der Gegner ist am Zug");
					}
				});
				RequestProtocol req = peer.readReq();
				boolean istGetroffen = eigeneMap.istGetroffen(req.getX(), req.getY());
				try {
					peer.sendRes(new ResponseProtocol(istGetroffen));
				} catch (IOException e) {
					// Verbindung unterbrochen, Hinweis auf GUI, erneuter Verbindungsaufbau
				}
				if(istGetroffen) {
					eigeneMap.paint(contr);
					SwingUtilities.invokeLater(new Runnable() {
						@Override 
						public void run() {
							if(eigeneMap.alleVersenkt()) {
								contr.spielendeAlert(false);
								// Spiel zuruecksetzen
								// evtl. neuer Verbindungsaufbau
			               	 }
							 warten();
						}
			         });
					// GUI anzeigen, dass nochmal gewartet werden muss bzw. der Gegner am Zug ist
				} else {
					amZug = true;
					eigeneMap.paint(contr);
					SwingUtilities.invokeLater(new Runnable() {
						@Override 
						public void run() {
			               	 if(eigeneMap.alleVersenkt()) {
								contr.spielendeAlert(false);
								// Spiel zuruecksetzen
								// evtl. neuer Verbindungsaufbau
			               	 }
			               	 zugAusfuehren();
						}
					});
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	public boolean proofIP(String ipString) {
		return Peer.proofIP(ipString);
	}
	
	public void verbindungUnterbrochen() {
		//GUI "einfrieren" lassen
	}
}
