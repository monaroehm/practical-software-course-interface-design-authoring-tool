import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AuthoringTool extends Application {


    /**
     * Sets up a new Stage width fixed width and height.
     *
     * @param primaryStage shall be the main window.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("Authoring Tool");
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main function. Starts the FX app.
     * @param args Standart String[] args.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
