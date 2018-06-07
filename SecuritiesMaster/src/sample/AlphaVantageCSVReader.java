package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AlphaVantageCSVReader extends CSVReader {

    private String FilePath;
    private String Separator;
    public AlphaVantageCSVReader(String filePath, String separator) {
        FilePath = filePath;
        Separator = separator;
    }

    public void printAllCSVLines()throws FileNotFoundException, IOException {
        BufferedReader br;
        br = getBufferedReader(FilePath);

        String line = "";
        while ((line = br.readLine()) != null) {
            // While there are still lines to read in BufferedReader,
            // Print out the data from that line
            String[] data = line.split(this.Separator);
            System.out.println(data);
        }


    }
}

//    String csvFilePath = "stockData.csv";
//    BufferedReader br = null;
//    String line = "";
//    String separator = ",";
//
//        br = new BufferedReader(new FileReader(csvFilePath));
//                while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] data = line.split(separator);
//                try {
//                float dataPoint = Float.valueOf(data[1]);
//                stockData.add(dataPoint);
//                }
//                catch (NumberFormatException nfe) {
//                System.out.println("Error: Not a number");
//                }
