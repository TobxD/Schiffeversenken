package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import logic.Schiffeversenken;

public class Server extends Peer {

	private final ServerSocket server; 
	
    public Server(Schiffeversenken ss) throws IOException { 
    	super(ss);
        server = new ServerSocket(21235);
        socket = null;
    } 

    public boolean connect() {
    	try {
    		while(socket == null) {
    			System.out.println("Server| Wartet auf Verbindung");
    			socket = server.accept();
    			try {
    				out = new ObjectOutputStream(socket.getOutputStream());
    				in = new ObjectInputStream(socket.getInputStream());
    			} catch (IOException e) {
    				connect();
    			}
    			System.out.println("Server| Verbindung hergestellt und Streams erzeugt");
    			ss.verbindungHergestellt();
    		}
    		return true;
    	} catch (IOException e) {
    		connect();
    		return false;
    	}
    } 
    
    public void disconnect() {
    	if (socket != null) {
            try { 
                socket.close(); 
                server.close();
            } catch (IOException e) { 
                disconnect(); 
            } 
    	}
    }
}
