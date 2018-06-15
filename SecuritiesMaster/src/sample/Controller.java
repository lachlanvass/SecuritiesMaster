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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.AlphaVantage.AlphaVantageCSVReader;
import sample.AlphaVantage.AlphaVantageQueryMonthly;
import sample.StockChart.MainStockChart;
import sample.StockChart.SideMenuStockChart;

import java.io.*;
import java.util.ArrayList;

public class Controller {

    double LINE_CHART_SIZE = 0.2;
    byte graph_row_index = 0;
    @FXML private TextField tf_stock_searcher;

    private double[] dataSet = new double[6];
    boolean scrollBarGenerated = false;

    ArrayList<LineChart> Charts = new ArrayList<>();
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

    protected void querySymbol() throws IOException{
        String SymbolResult = tf_stock_searcher.getText();
        AlphaVantageQueryMonthly avQuery = new AlphaVantageQueryMonthly(SymbolResult, "UZWQWLMCV2TNGH7T");
        System.out.println(avQuery.getQuery());
        avQuery.submitQuery();
        System.out.println("Done");
    }

    private void spawnMainLineChart(String chartName, String xAxisLabel,
                                    String yAxisLabel){

        // Configure chart
        MainStockChart mainChart = new MainStockChart(FocusedData);
        mainChart.setXAxisLabel(xAxisLabel);
        mainChart.setYAxisLabel(yAxisLabel);
        mainChart.setChartName(chartName);

        // remove chart from main grid - overwriting it
        // Otherwise draws new graph atop the old one.
        grid_main.getChildren().remove(line_chart_main);
        line_chart_main = mainChart.getLineChartData();
        grid_main.getChildren().remove(line_chart_main);
        grid_main.add(line_chart_main, 2, 3);
    }

    @FXML protected void spawnSideLineGraph (ActionEvent event) throws IOException{

        String searchInput = tf_stock_searcher.getText();
        AlphaVantageQueryMonthly avQuery = new AlphaVantageQueryMonthly(searchInput, "UZWQWLMCV2TNGH7T");
        avQuery.submitQuery();

        AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader(avQuery.fileName, ",");
        ArrayList<Number> volumeData;
        volumeData = AlphaCSV.getVolume();
        FocusedData = volumeData;

        // Give the side stock chart all relevant information
        // So that when clicked it can be loaded from each instance
        // And given to the Main stock chart
        SideMenuStockChart sideMenuChart = new SideMenuStockChart(FocusedData);
        sideMenuChart.setChartName(avQuery.getSymbolInput());
        sideMenuChart.setXAxisLabel(avQuery.getIntervalString());
        sideMenuChart.setYAxisLabel(AlphaCSV.getMeasurementType());

        LineChart chart = sideMenuChart.getLineChartData();
        Charts.add(chart);

        chart.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {

                spawnMainLineChart(
                        sideMenuChart.getChartName(),
                        sideMenuChart.getXAxisLabel(),
                        sideMenuChart.getYAxisLabel()
                );
            }
        });
        //LineChart chart = new RandomGraph().getGraph(true, dataSet );
        chart.setMaxSize(LINE_CHART_SIZE, LINE_CHART_SIZE);

        /*
         * ScrollPane can only contain 1 node. So must use a VBox which will dynamically load
         * in more charts. This is done by adding the charts to an ArrayList and adding them all
         * with each call of this method
          * */
        ScrollPane scrollPane = new ScrollPane();

        VBox vb_graphBox = new VBox();
        vb_graphBox.setSpacing(10);

        // Dynamically add charts to vBox
        for (LineChart lineChart : Charts) {
            vb_graphBox.getChildren().add(0, lineChart);
        }
        vb_graphBox.setPadding(new Insets(10));

        scrollPane.setContent(vb_graphBox);
        scrollPane.setPannable(true);
        grid_main.getChildren().add(scrollPane);

    }
    @FXML protected void deleteGraph(ActionEvent event) {
        Charts.remove(0);
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
