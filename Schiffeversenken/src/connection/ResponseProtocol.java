package connection;

import java.io.Serializable;

public class ResponseProtocol implements Serializable {

	private static final long serialVersionUID = 2942634479741780592L;
	private boolean getroffen;
	
	public ResponseProtocol(boolean getroffen) {
		this.getroffen = getroffen;
	}
	
	public boolean isGetroffen() {
		return getroffen;
	}
}
