package sample.StockChart;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.concurrent.Callable;

// BorderPane class which contains a Line Chart and a button which deletes that class
public class LineChartWithButton extends BorderPane {

    // Pass in a line chart, as well as an additional function to call when the button is pressed
    public LineChartWithButton(LineChart lineChart, Callable<String> additionalFunction) {
        Button deleteButton = new Button("-");
        deleteButton.setOnAction(new EventHandler<ActionEvent>()  {
            @Override
            public void handle(ActionEvent event) {
                LineChartWithButton parent = LineChartWithButton.this;

                // Call the additional function pass as a parameter to this constructor
                try {
                    additionalFunction.call();
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                parent.getChildren().removeAll(lineChart,deleteButton);
            }
        });

        this.setCenter(lineChart);
        this.setTop(deleteButton);
    }



}
