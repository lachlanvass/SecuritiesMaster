package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.json.*;
import java.io.BufferedReader;
import java.util.ArrayList;

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
