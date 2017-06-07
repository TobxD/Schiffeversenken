import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class OnlineGegner extends Gegner{
	public static void main(String[] args){
		System.out.println("laeuft");
		try {
			ServerSocket server = new ServerSocket(5555);
			Socket client = server.accept();
			PrintWriter w = new PrintWriter(client.getOutputStream(), true);
			//BufferedReader in = new BufferedReader(new InputStreamReader(System.in)));
			w.println("Hallo Dennis :PP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean beschiessen(int y, int x) {
		// TODO Auto-generated method stub
		return false; 
	}
}