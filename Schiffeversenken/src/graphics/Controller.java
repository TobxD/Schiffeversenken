package graphics;

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
	
	public Controller(){
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				JButton button = new JButton();
				//button.setOpacity(0.9);
				button.setPreferredSize(new Dimension(50,50));
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						JButton button = (JButton)event.getSource();
		            	ss.beschiessen(GridLayout.getColumnIndex(button), GridPane.getRowIndex(button));
					}
		        });
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
		
		Task<Void> task = new Task<Void>() {
			final DecimalFormat format = new DecimalFormat("00"); 
			  @Override
			  public Void call() throws Exception {
			    int i = 0;
			    while (true) {
			      final int finalI = i;
			      Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			        	spieldauerLabel.setText("Spieldauer: " + format.format(finalI/60) + ":" + format.format(finalI%60) + " Min");
			        }
			      });
			      i++;
			      Thread.sleep(1000);
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
				"Neues Spiel eröffnen oder Spiel beitreten?",
				"Schiffeversenken",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, imgIcon,
				options, null);
		
		if(option == JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(this, "Diese Adresse auf anderem Computer eingeben, um verbinden zu können: " + ipString,
					"IP-Adresse für Client", JOptionPane.INFORMATION_MESSAGE, imgIcon);
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
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
		if(amZug) {
			for(Node button : gegnMap.getChildren()) {
				button.setDisable(false);
			}
		} else {
			for(Node button : gegnMap.getChildren()) {
				button.setDisable(true);
			}
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
}
