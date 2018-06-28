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
import sample.StockChart.StockChart;

import java.io.*;
import java.util.ArrayList;

public class Controller {

    /* FXML UI COMPONENT REFERENCES */
    @FXML private TextField tf_stock_searcher;
    @FXML private BorderPane border_main;
    @FXML private Button btn_login;
    @FXML private Button btn_back;
    @FXML private TextField tf_login_username;
    @FXML private LineChart line_chart_main;
    @FXML private Label lbl_warning;
    @FXML private ToggleGroup radioGroup_dataType;
    @FXML private ToggleGroup radioGroup_timeFrame;


    /* ---------------------- GLOBAL DATA ------------------ */

    // Selected chart properties
    private String selectedChartFilePath;
    private String selectedChartName;
    private String selectedChartXAxisName;
    private String selectedChartYAxisName;
    private String selectedChartMeasurementType;
    private ArrayList<Number> selectedChartDataSet; // This will be used for displaying data - Main Chart, nothing else

    static private String sideLineChartFilePath;
    static private String sideChartMeasurementType;

    static private UserNameManager userNameManager = new UserNameManager();
    private final double LINE_CHART_SIZE = 0.2;

    static private AlphaVantageQuery alphaVantageQueryManager = new AlphaVantageQuery();

    private ArrayList<LineChart> loadedCharts = new ArrayList<>();

    final String APIKEY = "UZWQWLMCV2TNGH7T";


    /*-------------------- METHODS -------------------------------- */


    /* RADIO BUTTON METHODS */
    private String getTimeFrameRadioValue(){
        String result;
        RadioButton selectedTimeFrameRadio = (RadioButton) radioGroup_timeFrame.getSelectedToggle();
        String selectedTimeFrameRadioString = selectedTimeFrameRadio.getText();

        result = selectedTimeFrameRadioString;
        return result;
    }
    private String getDataTypeRadioValue() {
        String result;
        RadioButton selectedDataTypeRadio = (RadioButton) radioGroup_dataType.getSelectedToggle();
        String selectedDataTypeRadioString = selectedDataTypeRadio.getText();

        result = selectedDataTypeRadioString;
        return result;
    }


    private void setSelectedChartMeasurementType(String input) {
        this.selectedChartMeasurementType = input;
    }

    /* DATA PREPARATION METHODS */
    private ArrayList<Number> getAlphaVantageDataSet(String fileName) throws IOException {
        ArrayList<Number> result = new ArrayList<>();
        AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader(fileName, ",");

        switch (getDataTypeRadioValue()) {
            case "Open"     : result = AlphaCSV.getOpenPrices();
                break;
            case "Close"    : result = AlphaCSV.getClosePrices();
                break;
            case "High"     : result = AlphaCSV.getHighPrices();
                break;
            case "Low"      : result = AlphaCSV.getLowPrices();
                break;
            case "Volume"   : result = AlphaCSV.getVolume();
                break;
        }

        System.out.println(AlphaCSV.getMeasurementType());
        //setSelectedChartMeasurementType(AlphaCSV.getMeasurementType());
        sideChartMeasurementType = AlphaCSV.getMeasurementType();

        return result;
    }

    private void submitAlphaVantageQuery() throws IOException {

        String searchInput = tf_stock_searcher.getText();
        AlphaVantageQuery alphaVantageQueryManager = new AlphaVantageQuery();
        switch (getTimeFrameRadioValue()) {

            case "Intraday"     : alphaVantageQueryManager = new AlphaVantageQueryIntraDay(searchInput, APIKEY, "30min");
                break;
            case "Daily"        : alphaVantageQueryManager = new AlphaVantageQueryDaily(searchInput, APIKEY);
                break;
            case "Weekly"       : alphaVantageQueryManager = new AlphaVantageQueryWeekly(searchInput, APIKEY);
                break;
            case "Monthly"      : alphaVantageQueryManager = new AlphaVantageQueryMonthly(searchInput, APIKEY);
                break;
        }

        alphaVantageQueryManager.submitQuery();
        sideLineChartFilePath = alphaVantageQueryManager.getFileName();
    }

    private void setSelectDataSet(ArrayList<Number> dataSet) {
        this.selectedChartDataSet = dataSet;
    }

    private void spawnMainChart() throws IOException {

        // Configure chart
        MainStockChart mainChart = new MainStockChart(selectedChartDataSet);
        mainChart.setXAxisLabel(selectedChartXAxisName);
        mainChart.setYAxisLabel(selectedChartYAxisName);
        mainChart.setChartName(selectedChartName);
        //mainChart.setMainDataSet(selectedChartDataSet);

        border_main.getChildren().remove(line_chart_main);
        line_chart_main = mainChart.getLineChartData();
       // border_main.getChildren().remove(line_chart_main);
        border_main.setCenter(line_chart_main);
    }

    private void updateSelectedChartData(String chartName, String xAxisName, String yAxisName,
                                         String measurementType, String filePath, ArrayList<Number> stockData) {
        selectedChartName = chartName;
        selectedChartXAxisName = xAxisName;
        selectedChartYAxisName = yAxisName;
        selectedChartMeasurementType = measurementType;
        selectedChartFilePath = filePath;
        selectedChartDataSet = stockData;
    }

    private void appendThisUserStockDataFile(String fileName) throws IOException {

        boolean fileNameInUserStockDataFiles = userNameManager.getStockDataFiles(userNameManager.getUser()).contains(fileName);

        if (!fileNameInUserStockDataFiles) {
            // If this query is for data which is not already stored, append it to the users stock data file.
            userNameManager.appendStockDataFile(userNameManager.getUser(), fileName);
            userNameManager.appendToStockDataFilePaths(sideLineChartFilePath);
        }
    }

    @FXML protected void saveChartToUserFiles(ActionEvent event) throws IOException {

        appendThisUserStockDataFile(selectedChartFilePath);
        //IOMethods.appendThisUserStockDataFile(selectedChartFilePath, );

    }

    // TODO need method to save line chart to user data
    @FXML protected void spawnSideLineGraph (ActionEvent event) throws IOException {

        submitAlphaVantageQuery();
        ArrayList<Number> stockData = getAlphaVantageDataSet(sideLineChartFilePath); // This will set sideChartMeasurementType

        String symbolName = IOMethods.extractStockDataFileData(sideLineChartFilePath, "symbol name");
        String timePeriod = IOMethods.extractStockDataFileData(sideLineChartFilePath, "time period");

        lbl_warning.setVisible(false);

        try {
            submitAlphaVantageQuery();

            final SideMenuStockChart sideMenuChart = new SideMenuStockChart(stockData);
            sideMenuChart.setChartName(symbolName);
            sideMenuChart.setXAxisLabel(timePeriod);
            sideMenuChart.setYAxisLabel(sideChartMeasurementType);
            sideMenuChart.setFilePath(sideLineChartFilePath);
            LineChart chart = sideMenuChart.getLineChartData();

            chart.setOnMouseClicked(getGraphClickedHandler(sideMenuChart));
            chart.setMaxSize(LINE_CHART_SIZE, LINE_CHART_SIZE);

            loadedCharts.add(chart);
            addLineChartToSideMenu(loadedCharts);

        }
        catch (ArrayIndexOutOfBoundsException e) {

            lbl_warning.setVisible(true);
        }

    }

    @FXML protected void addLineChartToSideMenu(ArrayList<LineChart> chartList) {

        ScrollPane scrollPane = new ScrollPane();

        VBox vb_graphBox = new VBox();
        vb_graphBox.setSpacing(10);
        vb_graphBox.setPadding(new Insets(10));

        for (LineChart lineChart : chartList) {
            vb_graphBox.getChildren().add(0, lineChart);
        }

        scrollPane.setContent(vb_graphBox);
        scrollPane.setPannable(true);
        border_main.setLeft(scrollPane);
    }

    // Could add param for stock chart and move to separate file, static method
    private EventHandler getGraphClickedHandler(StockChart stockChart) {
        EventHandler event = new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                try {
                    updateSelectedChartData(
                            stockChart.getChartName(),
                            stockChart.getXAxisLabel(),
                            stockChart.getYAxisLabel(),
                            stockChart.getMeasurementType(),
                            stockChart.getFilePath(),
                            stockChart.getMainDataSet());
                    spawnMainChart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        return event;
    }

    @FXML protected void spawnRecognizedUserSideGraphs(ActionEvent event) throws IOException {

        ArrayList<String> userStockDataFiles = userNameManager.getStockDataFiles();
        ArrayList<String> filePaths = IOMethods.extractStockDataToList(userStockDataFiles, "file path");
        ArrayList<String> symbolNames = IOMethods.extractStockDataToList(userStockDataFiles, "symbol name");
        ArrayList<String> timePeriods = IOMethods.extractStockDataToList(userStockDataFiles, "time period");
        ArrayList<Number> stockData;
        short counter = 0;

        try {
            for (String stockDataFilePath : filePaths) {

                stockData = getAlphaVantageDataSet(stockDataFilePath); // This will set sideChartMeasurementType

                final SideMenuStockChart sideMenuChart = new SideMenuStockChart(stockData);
                sideMenuChart.setChartName(symbolNames.get(counter));
                sideMenuChart.setXAxisLabel(timePeriods.get(counter));
                sideMenuChart.setYAxisLabel(sideChartMeasurementType);
                sideMenuChart.setFilePath(stockDataFilePath);
                LineChart chart = sideMenuChart.getLineChartData();
                loadedCharts.add(chart);

                chart.setOnMouseClicked(getGraphClickedHandler(sideMenuChart));
                chart.setMaxSize(LINE_CHART_SIZE, LINE_CHART_SIZE);

                addLineChartToSideMenu(loadedCharts);
                counter++;
            }

        }
        catch (ArrayIndexOutOfBoundsException e) {
            lbl_warning.setVisible(true);
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

            if (userNameManager.usernameRecognized(tf_login_username.getText())) {
                // Set current user to username input

                // Save username from the login screen
                userNameManager.setUser(tf_login_username.getText());
                IOMethods.resolveStockDataFiles(userNameManager.getStockDataFiles(), APIKEY);

            }
            else {
                // save username and set it to current user
                userNameManager.saveUserName(tf_login_username.getText());
                userNameManager.setUser(tf_login_username.getText());
            }

            // Enter main screen
            stage = (Stage) btn_login.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        }
        else {
            // Got back to login screen
            stage = (Stage) btn_back.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("login.fxml"));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



}
//     selectedRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                System.out.println("Radio button changed - Main chart spawn function");
//
//        });
//

//  for (LineChart lineChart : Charts) {
//
//          LineChartWithButton lineChartBtn = new LineChartWithButton(lineChart, new Callable<String>() {
//@Override
//public String call() throws Exception {
//        // TODO only capable of deleting one graph file at a time.
//        return removeLineFromFile("UserData\\" + userNameManager.getUser() + "stockData.txt", stockDataFilePath);
//        }
//        });