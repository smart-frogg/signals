package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;



public class Main extends Application {
    private Stage primaryStage;
    private SplitPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Furie");

        initRootLayout();
    }

    /**
     * Инициализирует корневой макет.
     */
    public void initRootLayout() {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainForm.fxml"));
            rootLayout = (SplitPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);

            this.primaryStage = primaryStage;
			this.primaryStage.setTitle("Прототип классификатора растений");
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
			
			// init the controller
			FurieController controller = loader.getController();
			controller.setStage(this.primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public static void main(String[] args) {
		launch(args);
	}
}
