package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import logic.Schiffeversenken;

public class Client extends Peer {
	
	private final String ipString;
	
	public Client(Schiffeversenken ss, String ipString) {
		super(ss);
		this.ipString = ipString;
    } 
	
	public boolean connect() {
		try { 
            socket = new Socket(InetAddress.getByName(ipString), 21235);
            try {
            	out = new ObjectOutputStream(socket.getOutputStream());
    			in = new ObjectInputStream(socket.getInputStream());
    			
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            System.out.println("Client: Verbindung hergestellt und Streams erzeugt");
            ss.verbindungHergestellt();
            return true;
        } catch (UnknownHostException e) { 
            System.out.println("Unbekannter Server");
            return false;
        } catch (IOException e) { 
            System.out.println("IO-Probleme..."); 
            this.disconnect();
            ss.verbindungHerstellen(ipString);
            return false;
        }
	}
	
	/**
	 * Trennt die Verbindung zum anderen Peer
	 */
	public void disconnect() {
		if (socket != null) {
            try { 
                socket.close();
            } catch (IOException e) { 
                disconnect();
            }
    	}
	}
}
