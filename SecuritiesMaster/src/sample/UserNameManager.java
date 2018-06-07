package sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class UserNameManager {

    public void saveUserName(String userName) throws IOException {
        /* Saves string input to users.txt */
        try {
            FileWriter fileWriter = new FileWriter("users.txt", true);
            fileWriter.write(userName + "\n");
            fileWriter.close();
        }
        catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }


    public boolean usernameRecognized(String userName) throws IOException {
        /* Returns true if String username is in users.txt */
        boolean result = false;
        try {

            File file = new File("users.txt");
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
}
