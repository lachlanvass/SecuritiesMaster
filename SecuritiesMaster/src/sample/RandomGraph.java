package sample;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.Random;

public class RandomGraph {


    public LineChart<Number, Number> getGraph(boolean random, double[] dataSet)
    {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of month");

        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setTitle("Stock data");
        lineChart.setId("chart");


        XYChart.Series series = new XYChart.Series();

        if (random) {

            Random rand = new Random();

            for (int i = 0; i < 20; i++) {

                int x = rand.nextInt((100) + 1);
                series.getData().add(new XYChart.Data(i, x));

            }

        }
        else {

            for (int i = 0; i < dataSet.length; i++) {

                series.getData().add(
                        new XYChart.Data(i, dataSet[i]));
            }
        }

        lineChart.getData().add(series);


        return lineChart;

    }

    private double[] importDataSet(double[] dataSet) {

        return dataSet;
    }
}
