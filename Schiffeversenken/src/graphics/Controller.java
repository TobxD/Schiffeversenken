package graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import logic.Schiffeversenken;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Controller extends JFrame{
	private ImageIcon imgIcon = new ImageIcon("graphics/schiff.jpg");
	
	private Schiffeversenken ss;
	private Thread spieldauerThread;
	
	private JLabel verbindungLabel;
	private JLabel ipLabel;
	private JLabel spieldauerLabel;
	private JLabel zugaufforderungLabel;
	
	private JPanel eigeneMap;
	private JPanel gegnMap;
	
	private JLabel loadingImageView;
	
	// Test
	
	public Controller(){
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				JButton button = new JButton();
				//button.setOpacity(0.9);
				button.setPreferredSize(new Dimension(50,50));
				button.addActionListener(new ButtonListener(i,j));
				gegnMap.add(button, i, j);
			}
		}
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				JLabel imageView = new JLabel();
				//imageView.setOpacity(0.9);
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
				"Neues Spiel er�ffnen oder Spiel beitreten?",
				"Schiffeversenken",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, imgIcon,
				options, null);
		
		if(option == JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(this, "Diese Adresse auf anderem Computer eingeben, um verbinden zu k�nnen: " + ipString,
					"IP-Adresse f�r Client", JOptionPane.INFORMATION_MESSAGE, imgIcon);
			ss.verbindungHerstellen(null);
		} else {
			askForIPDialog("");
		} 
	}
	
	private void askForIPDialog(String hinweis) {
		String ip = (String) JOptionPane.showInputDialog(this, "Bitte gib eine IPv4-Adresse (mit Punkten) ein, um dich mit einem anderen Spiel zu verbinden:" + hinweis,
				"IP-Adresse", JOptionPane.PLAIN_MESSAGE, imgIcon, null, null);
		
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

	public void paintGegnMap(Image image, int index) {
		((Button)gegnMap.getChildren().get(index + 1)).setBackground(new Background(
					new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT))); // Index + 1, da das erste Element der ObservableList
	}					
	// ein Objekt der Klasse Group ist und kein Button

	public void paintEigeneMap(Image image, int index) {
		((ImageView)eigeneMap.getChildren().get(index + 1)).setImage(image);
	}
	
	public void buttonsSchalten(boolean amZug) {
		for(Component button : gegnMap.getComponents()) {
			((JButton) button).setEnabled(amZug);
		}
	}
	
	public void setZugstatus(String zugstatus) {
		zugaufforderungLabel.setText(zugstatus);
	}
	
	public void spielendeAlert(boolean gewonnen) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Schiffeversenken");
		if(gewonnen) {
			alert.setHeaderText("Sie haben gewonnen!");
		} else {
			alert.setHeaderText("Sie haben verloren!");
		}
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("graphics/schiff.jpg"));

		ButtonType buttonTypeOne = new ButtonType("Nochmal spielen");
		ButtonType buttonTypeTwo = new ButtonType("Beenden");

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == buttonTypeOne) {
			
		} else {
			Platform.exit();
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
