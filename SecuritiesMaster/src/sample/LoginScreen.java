package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginScreen extends GridPane {

    public void start() throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        GridPane grid = new GridPane();

        Label lb_user_name = new Label("UserName");
        grid.add(lb_user_name, 1, 0);

        TextField txt_user_name = new TextField();
        grid.add(txt_user_name, 1, 1);




    }

}
