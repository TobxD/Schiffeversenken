package graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import logic.Schiffeversenken;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Controller extends JFrame{
	private ImageIcon imgIcon = new ImageIcon("icon.jpg");
	
	private Schiffeversenken ss;
	private Thread spieldauerThread;
	
	private JLabel verbindungLabel;
	private JLabel ipLabel;
	private JLabel spieldauerLabel;
	private JLabel zugaufforderungLabel;
	
	private JPanel eigeneMap;
	private JPanel gegnMap;
	private JLabel[][] eigeneLabel = new JLabel[10][10];
	private JButton[][] gegnButtons = new JButton[10][10];
	
	public Controller(){
		eigeneMap = new JPanel();
		eigeneMap.setLayout(new GridLayout(10,10));
		gegnMap = new JPanel();
		gegnMap.setLayout(new GridLayout(10,10));
		
		verbindungLabel = new JLabel();
		ipLabel = new JLabel();
		spieldauerLabel = new JLabel();
		zugaufforderungLabel = new JLabel();
		
		JPanel south = new JPanel();
		south.add(verbindungLabel);
		south.add(ipLabel);
		south.add(spieldauerLabel);
		south.add(zugaufforderungLabel);
		south.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.add(new JLabel("Gegnerische Map"));
		left.add(gegnMap);
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.add(new JLabel("Eigene Map"));
		right.add(eigeneMap);
		
		JPanel center = new JPanel();
		center.add(left);
		center.add(right);
		
		JPanel borderPanel = (JPanel) this.getContentPane();
		borderPanel.setLayout(new BorderLayout());
		borderPanel.add(south, BorderLayout.SOUTH);
		borderPanel.add(center, BorderLayout.CENTER);
		
		this.setTitle("Schiffeversenken");
		this.setIconImage(imgIcon.getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
	    this.setVisible(true);

		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				JButton button = new JButton();
				//button.setOpacity(0.9);
				button.setPreferredSize(new Dimension(50,50));
				button.addActionListener(new ButtonListener(i,j));
				gegnButtons[i][j] = button;
				gegnMap.add(button, i, j);
			}
		}
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				JLabel imageView = new JLabel();
				//imageView.setOpacity(0.9);
				eigeneLabel[i][j] = imageView;
				eigeneMap.add(imageView, i, j);
			}
		}
		
		Runnable task = new Runnable() {
			final DecimalFormat format = new DecimalFormat("00"); 
			  @Override
			  public void run() {
			    int i = 0;
			    while (true) {
			      final int finalI = i;
			      SwingUtilities.invokeLater(new Runnable() {
			        @Override
			        public void run() {
			        	spieldauerLabel.setText("Spieldauer: " + format.format(finalI/60) + ":" + format.format(finalI%60) + " Min");
			        }
			      });
			      i++;
			      try {
					Thread.sleep(1000);
			      } catch (InterruptedException e) {}
			    }
			  }
			};
		spieldauerThread = new Thread(task);
		spieldauerThread.setDaemon(true);
		
		buttonsSchalten(false);
	}
	
	public void setSchiffeversenken(Schiffeversenken ss) {
		this.ss = ss;
	}
	
	public void askForPeer(String ipString) {
		Object[] options = {"Neues Spiel", "Spiel beitreten"};
		int option = JOptionPane.showOptionDialog(this,
				"Neues Spiel er\u00f6ffnen oder Spiel beitreten?",
				"Schiffeversenken",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, imgIcon,
				options, null);
		
		if(option == JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(this, "Diese Adresse auf anderem Computer eingeben, um verbinden zu k\u00f6nnen: " + ipString,
					"IP-Adresse f\u00fcr Client", JOptionPane.INFORMATION_MESSAGE, imgIcon);
			ss.verbindungHerstellen(null);
		} else {
			askForIPDialog("");
		} 
	}
	
	private void askForIPDialog(String hinweis) {
		String ip = (String) JOptionPane.showInputDialog(this, "Bitte gib eine IPv4-Adresse (mit Punkten) ein, um dich mit einem anderen Spiel zu verbinden:" + hinweis,
				"IP-Adresse", JOptionPane.PLAIN_MESSAGE, imgIcon, null, null);
		System.out.println(ss.proofIP(ip));
		if(ip.isEmpty()) {
			askForIPDialog("\nHinweis: IP-Adresse eingeben");
		} else if (ss.proofIP(ip)){
		    ss.verbindungHerstellen(ip);
		} else {
			askForIPDialog("\nHinweis: Falsche IP-Adresse");
		}
	}
	
	public void zeigeVerbindungsstatus(String status) {
		verbindungLabel.setText("Verbindung: " + status);
	}
	
	public void zeigeIPAdresse(String ip) {
		ipLabel.setText("IP-Adresse: " + ip);
	}
	
	public void zeigeSpieldauer() {
		spieldauerThread.start();
	}

	public void paintGegnMap(Image image, int x, int y) {
		gegnButtons[y][x].setIcon(new ImageIcon(image));
	}
	// ein Objekt der Klasse Group ist und kein Button

	public void paintEigeneMap(Image image, int x, int y) {
		eigeneLabel[y][x].setIcon(new ImageIcon(image));
	}
	
	public void buttonsSchalten(boolean amZug) {
		for(JButton[] bs : gegnButtons){
			for(JButton b : bs){
				b.setEnabled(amZug);
			}
		}
	}
	
	public void setZugstatus(String zugstatus) {
		zugaufforderungLabel.setText(zugstatus);
	}
	
	public void spielendeAlert(boolean gewonnen) {
		Object[] options = {"Nochmal spielen" , "Beenden"};
		int option;
		if(gewonnen) {
			option = JOptionPane.showOptionDialog(this,
					"Sie haben gewonnen",
					"Schiffeversenken",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, imgIcon,
					options, null);
		} else {
			option = JOptionPane.showOptionDialog(this,
					"Sie haben verloren",
					"Schiffeversenken",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, imgIcon,
					options, null);
		}
		if(option == JOptionPane.YES_OPTION) {
			//TODO spiel neu starten
		} else {
			System.exit(0);
		}
	}
	
	class ButtonListener implements ActionListener{
		int r, c;
		public ButtonListener(int r, int c){
			this.r = r;
			this.c = c;
		}
		@Override
		public void actionPerformed(ActionEvent event) {
        	ss.beschiessen(c, r);
		}
    }
}
