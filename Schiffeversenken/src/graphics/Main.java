package graphics;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Schiffeversenken;

public class Main extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"Schiffeversenken.fxml"));
		Parent root = loader.load();
		new Schiffeversenken((Controller) loader.getController());
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinHeight(700);
		stage.setMinWidth(1200);
		stage.getIcons().add(new Image("graphics/schiff.jpg"));
		stage.setTitle("Schiffeversenken");
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
			    Platform.exit();
			    System.exit(0);
			}
		});
		stage.show();
	}
	
	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
