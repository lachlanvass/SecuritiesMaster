package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class CSVReader {

    private String FilePath;
    private String Separator;
    public CSVReader() {
        // Default constructor for inheritance
    }
    public CSVReader(String filePath, String separator) {
        FilePath = filePath;
        Separator = separator;
    }

    public BufferedReader getBufferedReader(String filePath) throws FileNotFoundException {
        BufferedReader br;
        FileReader csvFileReader = new FileReader(this.FilePath);
        br = new BufferedReader(csvFileReader);

        return br;
    }
}
//
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
