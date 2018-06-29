package sample;

import sample.AlphaVantage.*;

import java.io.*;
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class IOMethods {

    public static void resolveStockDataFiles(ArrayList<String> stockDataFiles, String APIKEY) throws IOException {

        /*
            This method ensures that when a user logs in, the stock data csv files they have
            access too actually exist.
         */

        // Get user name
        // Need to get a list of files the user has

        for (String stockDataFileString : stockDataFiles) {

            String timePeriod = extractStockDataFileData(stockDataFileString, "time period");
            String symbolName = extractStockDataFileData(stockDataFileString, "symbol name");
            // Populate list of files for use to draw them to side stock chart bar

            AlphaVantageQuery avQuery = new AlphaVantageQuery();
        if (timePeriod != null) {

            switch (timePeriod) {

                case "INTRADAY":
                    avQuery = new AlphaVantageQueryIntraDay(symbolName, APIKEY, "30min");
                    break;
                case "DAILY":
                    avQuery = new AlphaVantageQueryDaily(symbolName, APIKEY);
                    break;
                case "WEEKLY":
                    avQuery = new AlphaVantageQueryWeekly(symbolName, APIKEY);
                    break;
                case "MONTHLY":
                    avQuery = new AlphaVantageQueryMonthly(symbolName, APIKEY);
                    break;
            }
            }

            avQuery.submitQuery();

        }

    }

    public static String extractStockDataFileData(String filePath, String valueToReturn) {


        /* VALUE TO RETURN OPTIONS: symbol name, time period, file path */
        ArrayList<String> validOptions = new ArrayList<>();
        validOptions.add("symbol name");
        validOptions.add("time period");
        validOptions.add("file path");

        if (!validOptions.contains(valueToReturn)) {
            throw new InvalidParameterException("Valid params are: symbol name, time period, file path");
        }

        String result = new String();

        filePath = filePath.replace("StockData\\data", "");
        filePath = filePath.replace("-TIME_SERIES_", " ");
        filePath = filePath.replace(".csv", "");

        String[] st = filePath.split("\\s+", 15);

        String symbolName = st[0];
        String timePeriod = st[st.length - 1];
        String completeFilePath = "StockData\\data" + symbolName + "-TIME_SERIES_" + timePeriod + ".csv";

        switch (valueToReturn) {

            case ("symbol name") : result = symbolName;          break;
            case ("time period") : result = timePeriod;          break;
            case ("file path")   : result = completeFilePath;    break;
        }


        return result;
    }

    public static ArrayList<String> extractStockDataToList(ArrayList<String> files, String valueToReturn) {


        /* VALUE TO RETURN OPTIONS: symbol name, time period, file path */
        ArrayList<String> validOptions = new ArrayList<>();
        validOptions.add("symbol name");
        validOptions.add("time period");
        validOptions.add("file path");

        if (!validOptions.contains(valueToReturn)) {
            throw new InvalidParameterException("Valid params are: symbol name, time period, file path");
        }

        ArrayList<String> result = new ArrayList<>();

        for (String filePath : files) {

            filePath = filePath.replace("StockData\\data", "");
            filePath = filePath.replace("-TIME_SERIES_", " ");
            filePath = filePath.replace(".csv", "");

            String[] st = filePath.split("\\s+", 15);

            String symbolName = st[0];
            String timePeriod = st[st.length - 1];
            String completeFilePath = "StockData\\data" + symbolName + "-TIME_SERIES_" + timePeriod + ".csv";

            switch (valueToReturn) {

                case ("symbol name") : result.add(symbolName);          break;
                case ("time period") : result.add(timePeriod);          break;
                case ("file path")   : result.add(completeFilePath);    break;
            }


        }

        return result;
    }

    public static void appendThisUserStockDataFile(String fileName, String sideLineChartFilePath,
                                                   UserNameManager userNameManager) throws IOException {

        boolean fileNameInUserStockDataFiles = userNameManager.getStockDataFiles(userNameManager.getUser()).contains(fileName);

        if (!fileNameInUserStockDataFiles) {
            // If this query is for data which is not already stored, append it to the users stock data file.
            userNameManager.appendStockDataFile(userNameManager.getUser(), fileName);
            //userNameManager.appendToStockDataFilePaths(sideLineChartFilePath);
        }
    }



    public static String removeLineFromFile(String file, String lineToRemove) {

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
}
