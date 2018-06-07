package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.json.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Controller {

    double LINE_CHART_SIZE = 0.2;
    byte graph_row_index = 0;
    @FXML private TextField tf_stock_searcher;

    private double[] dataSet = new double[6];

    @FXML private GridPane grid_login;
    @FXML private GridPane grid_main;
    @FXML private Button btn_login;
    @FXML private Button btn_back;
    @FXML private TextField tf_login_username;
    @FXML private LineChart line_chart_main;

    private VBox vb_graphBox = new VBox();
    @FXML protected void handleStockSearch(ActionEvent action) {

        System.out.println(tf_stock_searcher.getText());

    }

    private String getStockSearcherValue(ActionEvent event) {

        String result = tf_stock_searcher.getText();
        return result;

    }

    @FXML protected void mouseClickEventHandle(javafx.scene.input.MouseEvent event) {
        System.out.println(event.getSource());
    }

    private void populateMainLineChart(double[] MainChartData) {
        // Populate the main line chart with data
        // Used in onMouseClicked event handlers for side line charts

        // remove chart from main grid - overwriting it
        // Otherwise draws new graph atop the old one.
        grid_main.getChildren().remove(line_chart_main);
        RandomGraph randomGraph = new RandomGraph();
        line_chart_main = randomGraph.getGraph(false, MainChartData);
        grid_main.add(line_chart_main, 1, 5);
    }

    @FXML protected void spawnGraph (ActionEvent event) throws FileNotFoundException, IOException{

        // Handle graph drawing events
        dataSet[0] = 10;
        dataSet[1] = 15;
        dataSet[2] = 20;
        dataSet[3] = 1;
        dataSet[4] = 23;
        dataSet[5] = 12;

        ArrayList<Number> stockData = new ArrayList<Number>();
        String csvFilePath = "stockData.csv";
        BufferedReader br = null;
        String line = "";
        String separator = ",";

        br = new BufferedReader(new FileReader(csvFilePath));
        while ((line = br.readLine()) != null) {

            // use comma as separator
            String[] data = line.split(separator);
            try {
                float dataPoint = Float.valueOf(data[1]);
                stockData.add(dataPoint);
            }
            catch (NumberFormatException nfe) {
                System.out.println("Error: Not a number");
            }
        }

        br.close();

        for (Number f: stockData
             ) {

            System.out.println(f);

        }

        RandomGraph randomGraph = new RandomGraph();


        LineChart chart;
        if (tf_stock_searcher.getText().equals("SEC")){
            chart = randomGraph.getGraph(false, dataSet);
        }

        else {
            chart = randomGraph.getGraph(true, dataSet);
        }
        ArrayList<Number> chartData = new ArrayList<Number>();
        //chartData = chart.getData().toArray();
        chart.setMaxSize(LINE_CHART_SIZE, LINE_CHART_SIZE);
        // Declare event so that when chart is clicked, the main chart assumes its data.
        chart.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {

               // populateMainLineChart();
            }
        });


        VBox vb_graphBox = new VBox();
        vb_graphBox.setSpacing(10);
        vb_graphBox.setPadding(new Insets(0, 20, 10, 20));
        vb_graphBox.getChildren().add(chart);
        grid_main.add(vb_graphBox, 0, graph_row_index);
        graph_row_index++;

    }

    protected void loadGraphsFromFile() {
        RandomGraph randomGraph = new RandomGraph();

        for (int i = 0; i < 4; i++) {
            VBox vb_graphBox = new VBox();
            vb_graphBox.setSpacing(10);
            vb_graphBox.setPadding(new Insets(0, 20, 10, 20));
            LineChart chart = randomGraph.getGraph(false, dataSet);
            vb_graphBox.getChildren().add(chart);
            grid_main.add(vb_graphBox, 0, graph_row_index);
            graph_row_index++;
        }
    }
    @FXML
    protected void switchSceneButtonAction(ActionEvent event) throws IOException {
        /* This method demonstrates changing scenes and loading a new FXML file*/


        Stage stage;
        Parent root;
        UserNameManager userNameManager = new UserNameManager();
        // Two different buttons in two different fxml files call this method
        // Therefore must use the references declared at the beginning of the class

        if (event.getSource() == btn_login) {

            if (userNameManager.usernameRecognized(tf_login_username.getText())) {
                System.out.println("Recognized: " + tf_login_username.getText());
            }
            else {
                userNameManager.saveUserName(tf_login_username.getText());
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

}
