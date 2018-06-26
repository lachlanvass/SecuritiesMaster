package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import sample.StockChart.LineChartWithButton;
import sample.StockChart.MainStockChart;
import sample.StockChart.SideMenuStockChart;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Controller {

    double LINE_CHART_SIZE = 0.2;
    @FXML private TextField tf_stock_searcher;
    ArrayList<LineChart> Charts = new ArrayList<>();
    @FXML private BorderPane border_main;
    @FXML private Button btn_login;
    @FXML private Button btn_back;
    @FXML private TextField tf_login_username;
    @FXML private LineChart line_chart_main;
    @FXML private Label lbl_warning;
    @FXML private ToggleGroup radioGroup_DateType;
    @FXML private ToggleGroup radioGroup_timeFrame;
    final private UserNameManager userNameManager = new UserNameManager();
    @FXML private ArrayList<String> currentUserStockDataFiles = new ArrayList<>();
    private String selectedChartFilePath;


    ArrayList<Number> FocusedData;

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

    private ArrayList<Number> getMainChartDataSet(String fileName) throws IOException{

        ArrayList<Number> result = new ArrayList<>();
        RadioButton selectedRadioButton = (RadioButton) radioGroup_DateType.getSelectedToggle();
        String selectedRadioButtonString = selectedRadioButton.getText();

        AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader(fileName, ",");

        switch (selectedRadioButtonString) {
            case "Open"     : result = AlphaCSV.getOpenPrices(); System.out.println(selectedRadioButtonString);
                break;
            case "Close"    : result = AlphaCSV.getClosePrices(); System.out.println(selectedRadioButtonString);
                break;
            case "High"     : result = AlphaCSV.getHighPrices(); System.out.println(selectedRadioButtonString);
                break;
            case "Low"      : result = AlphaCSV.getLowPrices(); System.out.println(selectedRadioButtonString);
                break;
            case "Volume"   : result = AlphaCSV.getVolume(); System.out.println(selectedRadioButtonString);
                break;
        }

        return result;

    }


    private void spawnMainLineChart(String chartName, String xAxisLabel,
                                    String yAxisLabel, String sourceFile,
                                    ArrayList<Number> dataSet) throws IOException {

        RadioButton selectedRadioButton = (RadioButton) radioGroup_DateType.getSelectedToggle();
        //String selectedRadioButtonString = selectedRadioButton.getText();

        // TODO If toggle button changes, it should change the data which the Main chart displays.
        selectedRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("Radio button changed - Main chart spawn function");
                try {
                    FocusedData = getMainChartDataSet(sourceFile);
                    MainStockChart mainChart = new MainStockChart(FocusedData);
                    mainChart.setXAxisLabel(xAxisLabel);
                    mainChart.setYAxisLabel(yAxisLabel);
                    mainChart.setChartName(chartName);
                    mainChart.setMainDataSet(FocusedData);

                    // remove chart from main grid - overwriting it
                    // Otherwise draws new graph atop the old one.
                    border_main.getChildren().remove(line_chart_main);
                    line_chart_main = mainChart.getLineChartData();
                    border_main.getChildren().remove(line_chart_main);
                    border_main.setCenter(line_chart_main);
                }
                catch (IOException io) {
                    System.out.println("Cannot change main chart. File does not exist.");

                }


            }
        });

        //TODO add way to save graphs to users and load them in
        // Configure chart
        MainStockChart mainChart = new MainStockChart(FocusedData);
        mainChart.setXAxisLabel(xAxisLabel);
        mainChart.setYAxisLabel(yAxisLabel);
        mainChart.setChartName(chartName);
        mainChart.setMainDataSet(FocusedData);

        // remove chart from main grid - overwriting it
        // Otherwise draws new graph atop the old one.
        border_main.getChildren().remove(line_chart_main);
        line_chart_main = mainChart.getLineChartData();
        border_main.getChildren().remove(line_chart_main);
        border_main.setCenter(line_chart_main);
    }

    @FXML protected void spawnSideLineGraph (ActionEvent event) throws IOException{

        // TODO save file to user stock data.txt file
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

            case "Intraday"     : avQuery = new AlphaVantageQueryIntraDay(searchInput, apiKey, "30min");
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
            userNameManager.appendToStockDataFilePaths(avQuery.fileName);
            }


        lbl_warning.setVisible(false);

        try {
            // If search bar empty
            AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader(avQuery.fileName, ",");
            ArrayList<Number> stockData;

            // TODO feature: make it so that when main graph present, switching toggle changes data displayed
                // Therefore must be able to load all data for a graph and switch on the fly
                // Radio buttons must know which stock data is there.
            //  // Write custom Radiobutton class that takes a stock chart as input?
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
            selectedChartFilePath = avQuery.getFileName();

            LineChart chart = sideMenuChart.getLineChartData();
            Charts.add(chart);

            chart.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {

                    try {
                        spawnMainLineChart(
                                sideMenuChart.getChartName(),
                                sideMenuChart.getXAxisLabel(),
                                sideMenuChart.getYAxisLabel(),
                                selectedChartFilePath,
                                sideMenuChart.getMainDataSet()
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            border_main.setLeft(scrollPane);

            for (String s : userNameManager.stockDataFilePaths) {
                System.out.println(s);
            }

        }
        catch (ArrayIndexOutOfBoundsException e) {

            // TODO This is pretty slow. Alternative to try-catch? if-else?
            lbl_warning.setVisible(true);
        }

    }

    private ArrayList<String> getStockDataFiles() {
        ArrayList<String> userStockDataFiles = userNameManager.getStockDataFiles();
        ArrayList<String> result = new ArrayList<>();
        for (String stockDataFileString : userStockDataFiles) {
            // Use REGEX to get query parts
            // Change file name format to make splitting easier?

            // Remove prefix\
            stockDataFileString = stockDataFileString.replace("StockData\\data", "");
            stockDataFileString = stockDataFileString.replace("-TIME_SERIES_", " ");
            stockDataFileString = stockDataFileString.replace(".csv", "");

            String[] st = stockDataFileString.split("\\s+", 15);

            String symbolName = st[0];
            String timePeriod = st[st.length - 1];
            String completeFilePath = "StockData\\data" + symbolName + "-TIME_SERIES_" + timePeriod + ".csv";
            result.add(completeFilePath);
        }

        return result;
    }

    private ArrayList<String> getStockDataSymbolNames() {
        ArrayList<String> userStockDataFiles = userNameManager.getStockDataFiles();
        ArrayList<String> result = new ArrayList<>();
        for (String stockDataFileString : userStockDataFiles) {
            // Use REGEX to get query parts
            // Change file name format to make splitting easier?

            // Remove prefix\
            stockDataFileString = stockDataFileString.replace("StockData\\data", "");
            stockDataFileString = stockDataFileString.replace("-TIME_SERIES_", " ");
            stockDataFileString = stockDataFileString.replace(".csv", "");

            String[] st = stockDataFileString.split("\\s+", 15);

            String symbolName = st[0];
            result.add(symbolName);

        }

        return result;
    }

    private ArrayList<String> getStockDataTimePeriods() {
        ArrayList<String> userStockDataFiles = userNameManager.getStockDataFiles();
        ArrayList<String> result = new ArrayList<>();
        for (String stockDataFileString : userStockDataFiles) {
            // Use REGEX to get query parts
            // Change file name format to make splitting easier?

            // Remove prefix\
            stockDataFileString = stockDataFileString.replace("StockData\\data", "");
            stockDataFileString = stockDataFileString.replace("-TIME_SERIES_", " ");
            stockDataFileString = stockDataFileString.replace(".csv", "");

            String[] st = stockDataFileString.split("\\s+", 15);

            String timePeriod = st[st.length - 1];
            result.add(timePeriod);

        }

        return result;
    }
    private void resolveStockDataFiles() throws IOException{

        /*
            This method ensures that when a user logs in, the stock data csv files they have
            access too actually exist.
         */

        String apiKey = "UZWQWLMCV2TNGH7T";
        // Get user name
        // Need to get a list of files the user has
        ArrayList<String> userStockDataFiles = userNameManager.getStockDataFiles();

        for (String stockDataFileString : userStockDataFiles) {
            // Use REGEX to get query parts
            // Change file name format to make splitting easier?

            // Remove prefix
            stockDataFileString = stockDataFileString.replace("StockData\\data", "");
            stockDataFileString = stockDataFileString.replace("-TIME_SERIES_", " ");
            stockDataFileString = stockDataFileString.replace(".csv", "");

            String[] st = stockDataFileString.split("\\s+", 15);

            String symbolName = st[0];
            String timePeriod = st[st.length - 1];
            String completeFilePath = "StockData\\data" + symbolName + "-TIME_SERIES_" + timePeriod + ".csv";
            userNameManager.appendToStockDataFilePaths(completeFilePath);

            for (String s : userNameManager.stockDataFilePaths) {
                System.out.println(s + " from resolve method");
            }
            // Populate list of files for use to draw them to side stock chart bar

            AlphaVantageQuery avQuery = new AlphaVantageQuery();
            if (timePeriod != null) {

                switch (timePeriod) {

                    case "INTRADAY":
                        avQuery = new AlphaVantageQueryIntraDay(symbolName, apiKey, "30min");
                        break;
                    case "DAILY":
                        avQuery = new AlphaVantageQueryDaily(symbolName, apiKey);
                        break;
                    case "WEEKLY":
                        avQuery = new AlphaVantageQueryWeekly(symbolName, apiKey);
                        break;
                    case "MONTHLY":
                        avQuery = new AlphaVantageQueryMonthly(symbolName, apiKey);
                        break;
                }
            }

            avQuery.submitQuery();

        }

    }

    private String removeLineFromFile(String file, String lineToRemove) {

        String result;
        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                result = "false";
                return result;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                result = "false";
                return result;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            result = "true";
            System.out.println("Done again");
            return result;
        }
    }

    @FXML private void deleteSideStockChart(ActionEvent event) {
        // Button attached to each graph
        Object selectedGraph = event.getSource();
        //border_main.getChildren().remove(border_main.lookup(".button"));

    }

    @FXML protected void spawnRecognizedUserSideGraphs(ActionEvent event) throws IOException {

        // TODO in order to enable this to work, see need a function to ensure that the stock data files in each
        // TODO user txt file, actually exist in the stock data files.

        ArrayList<String> filePaths = getStockDataFiles();
        ArrayList<String> symbolNames = getStockDataSymbolNames();
        ArrayList<String> timePeriods = getStockDataTimePeriods();
        short counter = 0;
        try {
            for (String stockDataFilePath : filePaths) {
                System.out.println(stockDataFilePath);
                AlphaVantageCSVReader AlphaCSV = new AlphaVantageCSVReader(stockDataFilePath, ",");

                // TODO find a way to repord what type of data we want. Or a way to switch between data easily on a graph
                FocusedData = AlphaCSV.getVolume();

                SideMenuStockChart sideMenuChart = new SideMenuStockChart(FocusedData);
                sideMenuChart.setChartName(symbolNames.get(counter));
                sideMenuChart.setXAxisLabel(timePeriods.get(counter));

                LineChart chart = sideMenuChart.getLineChartData();
                Charts.add(chart);

                chart.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent event) {

                        try {
                            spawnMainLineChart(
                                    sideMenuChart.getChartName(),
                                    sideMenuChart.getXAxisLabel(),
                                    sideMenuChart.getYAxisLabel(),
                                    selectedChartFilePath,
                                    sideMenuChart.getMainDataSet()
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                chart.setMaxSize(LINE_CHART_SIZE, LINE_CHART_SIZE);

                ScrollPane scrollPane = new ScrollPane();

                VBox vb_graphBox = new VBox();
                vb_graphBox.setSpacing(10);

                // Dynamically add charts to vBox
                for (LineChart lineChart : Charts) {

                    LineChartWithButton lineChartBtn = new LineChartWithButton(lineChart, new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            // TODO only capable of deleting one graph file at a time.
                            return removeLineFromFile("UserData\\" + userNameManager.getUser() + "stockData.txt", stockDataFilePath);
                        }
                    });
                    vb_graphBox.getChildren().add(0, lineChartBtn);
                }

                vb_graphBox.setPadding(new Insets(10));

                scrollPane.setContent(vb_graphBox);
                scrollPane.setPannable(true);
                border_main.setLeft(scrollPane);
                counter++;
            }

        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught");
            // TODO This is pretty slow. Alternative to try-catch? if-else?
            lbl_warning.setVisible(true);
        }

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
                userNameManager.setUser(tf_login_username.getText());
                resolveStockDataFiles();

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
