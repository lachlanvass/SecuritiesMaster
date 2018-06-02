package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Controller {

    @FXML private TextField tf_stock_searcher;

    private double[] dataSet = new double[6];

    @FXML private GridPane grid_login;
    @FXML private Button btn_login;
    @FXML private Button btn_back;
    @FXML private TextField tf_login_username;
    @FXML protected void handleStockSearch(ActionEvent action) {

        System.out.println(tf_stock_searcher.getText());

    }

    private String getStockSearcherValue(ActionEvent event) {

        String result = tf_stock_searcher.getText();
        return result;

    }

    @FXML protected void spawnGraph (ActionEvent event) {

        dataSet[0] = 10;
        dataSet[1] = 15;
        dataSet[2] = 20;
        dataSet[3] = 1;
        dataSet[4] = 23;
        dataSet[5] = 12;
        String result = getStockSearcherValue(event);
        System.out.println(result);
        RandomGraph randomGraph = new RandomGraph();
        if (tf_stock_searcher.getText().equals("SEC")){
            System.out.println(true);
            LineChart chart = randomGraph.getGraph(false, dataSet);
            grid_login.add(chart, 1, 1);
        }
        else {
            LineChart chart = randomGraph.getGraph(true, dataSet);
            grid_login.add(chart, 1, 1);
        }

    }

    @FXML
    protected void switchSceneButtonAction(ActionEvent event) throws IOException {
        /* This method demonstrates changing scenes and loading a new FXML file*/


        Stage stage;
        Parent root;

        // Two different buttons in two different fxml files call this method
        // Therefore must use the references declared at the beginning of the class
        
        if (event.getSource() == btn_login) {

            if (usernameRecognized(tf_login_username.getText())) {
                Label warningLabel = new Label("User already found");
                warningLabel.setTextFill(Color.color(1.0, 0.1, 0.1));
                grid_login.add(warningLabel, 2, 3);
                System.out.println("Recognized: " + tf_login_username.getText());
            }
            else {
                saveUserName(tf_login_username.getText());
            }

            stage = (Stage) btn_login.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        }
        else {
            stage = (Stage) btn_back.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("login.fxml"));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void saveUserName(String userName) throws IOException {
        /* Saves string input to users.txt */
        try {
            FileWriter fileWriter = new FileWriter("users.txt", true);
            fileWriter.write(userName + "\n");
            fileWriter.close();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }


    private boolean usernameRecognized(String userName) throws IOException {
        /* Returns true if String username is in users.txt */
        boolean result = false;
        try {

            File file = new File("users.txt");
            Scanner scanner = new Scanner(file);

            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(userName)) {
                    result = true;
                    return result;

                }
                lineNum++;

            }

        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
            return result;
        }

        return result;

    }

}
