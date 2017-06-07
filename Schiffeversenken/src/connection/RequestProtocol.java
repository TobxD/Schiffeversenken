package connection;

import java.io.Serializable;

public class RequestProtocol implements Serializable {
	
	private static final long serialVersionUID = 8937037607309368084L;
	private int x;
	private int y;
	
	public RequestProtocol(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
