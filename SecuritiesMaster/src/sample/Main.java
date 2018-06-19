package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import sample.AlphaVantage.*;

public class Main extends Application {

    protected Stage stage;
    private String FXMLPath = "login.fxml";
    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource(FXMLPath));
        stage = primaryStage;

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) throws IOException {
        launch(args);

    }
}
