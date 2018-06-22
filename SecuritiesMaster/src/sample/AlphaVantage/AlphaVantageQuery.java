package sample.AlphaVantage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class AlphaVantageQuery {

    protected String QueryBase = "https://www.alphavantage.co/query?";
    protected String QueryEnd = "&datatype=csv";
    protected String QueryFunctionInput;

    protected String SymbolInput;
    protected String APIKeyInput;
    protected String intervalString;
    protected ArrayList<String> QueryInputs = new ArrayList<>();
    protected ArrayList<String> QueryParts = new ArrayList<>();
    public String fileName;

    public AlphaVantageQuery(String queryFunction, String symbol,
                                String apiKey) {

        QueryFunctionInput = queryFunction;
        SymbolInput = symbol;
        APIKeyInput = apiKey;

        populateQueryInputs();
        populateQueryParts();
        String query = buildQuery();
    }


    public AlphaVantageQuery() { /* default constructor */ }

    protected void populateQueryInputs() {
        QueryInputs.add(QueryFunctionInput);
        QueryInputs.add(SymbolInput);
        QueryInputs.add(APIKeyInput);

    }

    public String getFileName() {
        return fileName;
    }

    public String getIntervalString() {
        return intervalString;
    }

    protected void populateQueryParts() {
        QueryParts.add("function=");
        QueryParts.add("&symbol=");
        QueryParts.add("&apikey=");
        QueryParts.add(QueryEnd);
    }

    protected String buildQuery() {
        String result;
        StringBuilder queryStringBuilder = new StringBuilder();
        byte counter = 0;
        queryStringBuilder.append(QueryBase);

        for (String QueryPart : QueryParts) {

            /* Build query string
//             * Combine QueryParts with QueryInputs
//              * and the & character and = character.
//              * This fits AlphaVantage format */

            queryStringBuilder.append(QueryPart);
//            if (counter != finalLoopCount) {
//                // Do not add "=" on final loop
//                queryStringBuilder.append("=");
//            }
            try {
                queryStringBuilder.append(QueryInputs.get(counter));
            }
            catch (IndexOutOfBoundsException indexBounds) {
                System.out.println("Intended out of bounds error access. Skipping");
                System.out.println("In AlphaVantageQuery.java - buildQuery()");
            }

            counter++;
        }

        result = queryStringBuilder.toString();

        return result;
    }

    public String getQuery() {

        /* Utility class which allows users to view the built AlphaVantage query string */
        String result = buildQuery();
        return result;
    }

    public void submitQuery() throws IOException {
        String query = getQuery();
        InputStream inputStream = new URL(query).openStream();
        Scanner inputScanner = new Scanner(inputStream);
        String fileNameBase = "StockData\\";

        this.fileName = fileNameBase + "data"
                + this.SymbolInput + "-"
                + this.QueryFunctionInput
                + ".csv";

        FileWriter fileWriter = new FileWriter(this.fileName, false);

        // TODO catch potential exception non existent resource
        while (inputScanner.hasNext()) {
            String data = inputScanner.nextLine();
            String[] values = data.split(",");

            // Writes one line of string values to file, then a new line character
            String result;
            StringBuilder builder = new StringBuilder();
            for (String valueString : values) {
                builder.append(valueString);
                builder.append(",");
            }
            result = builder.toString();
            fileWriter.write(result + "\n");
        }

        inputScanner.close();
        fileWriter.close();
    }

    public String getSymbolInput() {
        return SymbolInput;
    }



}
