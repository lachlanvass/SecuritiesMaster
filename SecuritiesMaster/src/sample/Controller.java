package sample;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.json.*;

import java.io.*;
import java.util.ArrayList;

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

    ArrayList<Number> FocusedData;

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

    private void populateMainLineChart(){
        // Populate the main line chart with data
        // Used in onMouseClicked event handlers for side line charts

        // remove chart from main grid - overwriting it
        // Otherwise draws new graph atop the old one.

        StockChart stockChart = new StockChart(FocusedData);
        stockChart.setMainChart();
        grid_main.getChildren().remove(line_chart_main);
        line_chart_main = stockChart.getLineChartData();
        grid_main.getChildren().remove(line_chart_main);

        grid_main.add(line_chart_main, 1, 5);
    }

    @FXML protected void spawnGraph (ActionEvent event) throws FileNotFoundException, IOException{

        AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader("stockData.csv", ",");
        ArrayList<Number> openPriceData;
        openPriceData = AlphaCSV.getVolume();
        FocusedData = openPriceData;

        StockChart stockChart = new StockChart(openPriceData);

        LineChart chart = stockChart.getLineChartData();

        chart.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {

                populateMainLineChart();
            }
        });
        //LineChart chart = new RandomGraph().getGraph(true, dataSet );
        chart.setMaxSize(LINE_CHART_SIZE, LINE_CHART_SIZE);

        VBox vb_graphBox = new VBox();
        vb_graphBox.setSpacing(10);
        vb_graphBox.setPadding(new Insets(0, 20, 10, 20));
        vb_graphBox.getChildren().add(chart);
        grid_main.add(vb_graphBox, 0, graph_row_index);
        graph_row_index++;

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
