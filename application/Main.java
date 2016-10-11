package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;



public class Main extends Application {
    private Stage primaryStage;
   // private SplitPane rootLayout;
    private AnchorPane rootLayout;

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
            loader.setLocation(Main.class.getResource("lab2Form.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);

            this.primaryStage = primaryStage;
			this.primaryStage.setTitle("Обработка сигналов");
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
			
			// init the controller
			Lab2Controller controller = (Lab2Controller) loader.getController();
			controller.setStage(this.primaryStage);
            /*loader.setLocation(Main.class.getResource("MainForm.fxml"));
            rootLayout = (SplitPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);

            this.primaryStage = primaryStage;
			this.primaryStage.setTitle("Обработка сигналов");
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
			
			// init the controller
			FurieController controller = loader.getController();
			controller.setStage(this.primaryStage);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public static void main(String[] args) {
		launch(args);
	}
}
