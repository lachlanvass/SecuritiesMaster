package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.AlphaVantage.*;
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
    @FXML private BorderPane grid_main;
    @FXML private Button btn_login;
    @FXML private Button btn_back;
    @FXML private TextField tf_login_username;
    @FXML private LineChart line_chart_main;
    @FXML private HBox hbox_search_section;
    @FXML private Label lbl_warning;
    @FXML private ToggleGroup radioGroup_DateType;
    @FXML private ToggleGroup radioGroup_timeFrame;
    private String currentUser;
    private UserNameManager userNameManager = new UserNameManager();
    private ArrayList<String> currentUserStockDataFiles = new ArrayList<>();
    private boolean newUser;


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
                                    String yAxisLabel, ArrayList<Number> dataSet){

        // TODO add checkboxes to control what data to look at

        //TODO add way to save graphs to users and load them in
        // Configure chart
        MainStockChart mainChart = new MainStockChart(FocusedData);
        mainChart.setXAxisLabel(xAxisLabel);
        mainChart.setYAxisLabel(yAxisLabel);
        mainChart.setChartName(chartName);
        mainChart.setMainDataSet(dataSet);

        // remove chart from main grid - overwriting it
        // Otherwise draws new graph atop the old one.
        grid_main.getChildren().remove(line_chart_main);
        line_chart_main = mainChart.getLineChartData();
        grid_main.getChildren().remove(line_chart_main);
        grid_main.setCenter(line_chart_main);
    }

    @FXML protected void spawnSideLineGraph (ActionEvent event) throws IOException{

        String searchInput = tf_stock_searcher.getText();
        String apiKey = "UZWQWLMCV2TNGH7T";
        // Detect the selected radio button
        RadioButton selectedRadioButton = (RadioButton) radioGroup_DateType.getSelectedToggle();
        String selectedRadioButtonString = selectedRadioButton.getText();

        RadioButton selectedTimeFrameButton = (RadioButton) radioGroup_timeFrame.getSelectedToggle();
        String selectedTimeFrameString = selectedTimeFrameButton.getText();

        // Change query depending on time frame button selected.
        AlphaVantageQuery avQuery = new AlphaVantageQuery();
        switch (selectedTimeFrameString) {

            case "Intraday"     : avQuery = new AlphaVantageQueryIntraDay(searchInput, apiKey, "10min");
                                    break;
            case "Daily"        : avQuery = new AlphaVantageQueryDaily(searchInput, apiKey);
                                    break;
            case "Weekly"       : avQuery = new AlphaVantageQueryWeekly(searchInput, apiKey);
                                    break;
            case "Monthly"      : avQuery = new AlphaVantageQueryMonthly(searchInput, apiKey);
                                    break;
        }

        avQuery.submitQuery();
        if (!userNameManager.getStockDataFiles(userNameManager.getUser()).contains(avQuery.fileName)) {

            // If this query is for data which is not already stored, append it to the users stock data file.
            userNameManager.appendStockDataFile(userNameManager.getUser(), avQuery.fileName);
        }

        lbl_warning.setVisible(false);

        try {
            // If search bar empty
            AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader(avQuery.fileName, ",");
            ArrayList<Number> stockData;

            // TODO feature: make it so that when main graph present, switching toggle changes data displayed
                // Therefore must be able to load all data for a graph and switch on the fly
                // Radio buttons must know which stock data is there.
                // Write custom Radiobutton class that takes a stock chart as input?
            switch (selectedRadioButtonString) {
                case "Open"     : stockData = AlphaCSV.getOpenPrices();
                                    FocusedData = stockData;
                                    break;
                case "Close"    : stockData = AlphaCSV.getClosePrices();
                                    FocusedData = stockData;
                                    break;
                case "High"     : stockData = AlphaCSV.getHighPrices();
                                    FocusedData = stockData;
                                    break;
                case "Low"      : stockData = AlphaCSV.getLowPrices();
                                    FocusedData = stockData;
                                    break;
                case "Volume"   : stockData = AlphaCSV.getVolume();
                                    FocusedData = stockData;
                                    break;
            }

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
                            sideMenuChart.getYAxisLabel(),
                            sideMenuChart.getMainDataSet()
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
            grid_main.setLeft(scrollPane);

        }
        catch (ArrayIndexOutOfBoundsException e) {

            // TODO This is pretty slow. Alternative to try-catch? if-else?
            lbl_warning.setVisible(true);
        }

    }
    @FXML protected void deleteGraph(ActionEvent event) {
        Charts.remove(0);
    }

    @FXML protected void testCurrentUser(ActionEvent event) { System.out.println(userNameManager.getUser());}

    @FXML
    protected void switchSceneButtonAction(ActionEvent event) throws IOException {
        /* This method demonstrates changing scenes and loading a new FXML file*/


        Stage stage;
        Parent root;
        // Two different buttons in two different fxml files call this method
        // Therefore must use the references declared at the beginning of the class

        if (event.getSource() == btn_login) {

            if (userNameManager.usernameRecognized(tf_login_username.getText())) {
                // Set current user to username input

                // Save username from the login screen
                currentUser = tf_login_username.getText();
                userNameManager.setUser(tf_login_username.getText());
                currentUserStockDataFiles = userNameManager.getStockDataFiles(userNameManager.getUser());
                newUser = false;

                System.out.println("Recognized: " + tf_login_username.getText());
                System.out.println("current user = " + currentUser);
            }
            else {
                // save username and set it to current user
                userNameManager.saveUserName(tf_login_username.getText());
                currentUser = tf_login_username.getText();
                userNameManager.setUser(tf_login_username.getText());
                newUser = true;
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
