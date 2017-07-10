package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import logic.Schiffeversenken;

/**
 * Diese Klasse repräsentiert eine Seite der Spielverbindung. Sie bietet die Methoden zum Lesen
 * und Schreiben von Anfragen bzw. Antworten durch die IO-Streams.
 * 
 * @author Dennis Pollithy
 */

public abstract class Peer {

	protected Schiffeversenken ss;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;

	protected Peer(Schiffeversenken ss) {
		this.ss = ss;
		in = null;
		out = null;
	}
	
	protected Socket socket;

    public void sendReq(RequestProtocol req) throws IOException {
    	out.writeObject(req);
	}
    
    public RequestProtocol readReq() {
		RequestProtocol req = null;;
		try {
			req = (RequestProtocol)in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return req;
	}
    
    public void sendRes(ResponseProtocol res) throws IOException {
    	out.writeObject(res);
	}
    
    public ResponseProtocol readRes() {
    	ResponseProtocol res = null;
		try {
			res = (ResponseProtocol)in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("IO-Exception");
			e.printStackTrace();
			return null;
		}
		return res;
	}
    
    public static String getIP() {
    	String ip = null;
    	try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.err.println("Host unbekannt");
			e.printStackTrace();
		}
		return ip;
    }
    /**
     * Diese Methode überprüft, ob der Client erreichbar ist.
     * 
     * @param	die zu überprüfende IP-Adresse
     */
    public static boolean proofIP(String ipString) {
    	try {
			return InetAddress.getByName(ipString).isReachable(3000);
		} catch (UnknownHostException e) {
			System.err.println("Host unbekannt");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("IO-Exception");
			e.printStackTrace();
			return false;
		}
    }
}
