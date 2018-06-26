package sample;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserNameManager {

    // TODO ensure file paths work across platforms
    private static String currentUser = new String();
    private String userscsvPath = "UserData\\users.csv";

    public ArrayList<String> stockDataFilePaths = new ArrayList<>();
    public void saveUserName(String userName) throws IOException {
        /* User data for each user consists of a name and a pointer to a txt file.
            The txt files defines stock data which the user has saved
         */
        try {
            FileWriter fileWriter = new FileWriter(userscsvPath, true);
            String stockDataPointerString = userName + "stockData.txt";
            fileWriter.write(userName + "," + stockDataPointerString + "\n");
            generateStockDataFile(userName);
            fileWriter.close();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }

        private void generateStockDataFile(String userName) {

            try {
                String stockDataFile = userName + "stockData.txt";
                FileWriter fileWriter = new FileWriter("UserData\\" + stockDataFile, true);
                fileWriter.close();
            }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public void appendStockDataFile(String userName, String inputFileName) throws IOException {

        if (!usernameRecognized(userName)) {
            try {
                FileWriter fileWriter = new FileWriter("UserData\\" + getStockDataFileName(userName), true);
                fileWriter.write(inputFileName + "\n");
                fileWriter.close();
            }
            catch (IOException ioe) {
                System.err.println("IOException: " + ioe.getMessage());
            }
        }

    }

    public ArrayList<String> getStockDataFiles(String userName) {
        ArrayList<String> result = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader("UserData\\" + getStockDataFileName(userName));

            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.add(line);
            }
            fileReader.close();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

        return result;
    }

    public ArrayList<String> getStockDataFiles() {
        ArrayList<String> result = new ArrayList<>();
        String userName = this.currentUser;

        try {
            FileReader fileReader = new FileReader("UserData\\" + getStockDataFileName(userName));

            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.add(line);
            }
            fileReader.close();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

        return result;
    }

    private String getStockDataFileName (String userName) {

        String result = new String();

        /* Loop through username file, find user name, return String filepath of that user's
        * stockdata file.*/
        try {
            FileReader fileReader = new FileReader(userscsvPath);
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith(userName)) {
                    String[] data = line.split(",");
                    result = data[1];
                    fileReader.close();
                    return result;
                }


            }

        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

        return result;

    }

    public boolean usernameRecognized(String userName) throws IOException {
        /* Returns true if String username is in users.csv */
        boolean result = false;
        try {

            File file = new File(userscsvPath);
            Scanner scanner = new Scanner(file);

            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(userName)) {
                    result = true;
                    return result;
                }
                lineNum++;

            }

        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
            return result;
        }
        return result;
    }

    public void setUser(String input) {
        this.currentUser = input;
    }

    public String getUser() {
        return this.currentUser;
    }

    public ArrayList<String> getStockDataFilePaths() {
        return stockDataFilePaths;
    }

    public void setStockDataFilePaths(ArrayList<String> stockDataFilePaths) {
        this.stockDataFilePaths = stockDataFilePaths;
    }

    public void appendToStockDataFilePaths(String filePath) {
        this.stockDataFilePaths.add(filePath);
    }
}
