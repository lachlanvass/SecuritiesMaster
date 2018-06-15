package sample.StockChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SideMenuStockChart extends StockChart {


    public SideMenuStockChart(ArrayList<Number> dataSet) {
        super(dataSet);

    }

    public LineChart<Number, Number> getLineChartData()
    {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        // Set bounds for xAxis and yAxis based on the data in the graph
        // Configure yAxis and xAxis
        yAxis.setAutoRanging(false);
        // Use DATA_RANGE value to dynamically adjust how far the yAxis extends above and below
        // its bounds.

        if ((Y_AXIS_LBOUND.floatValue() - DATA_RANGE / 3) < 0) {
            // No point in setting the lower bound to less than 0,
            // Charts only deal in positive values
            yAxis.setLowerBound(0);
        }
        else {
            yAxis.setLowerBound(Y_AXIS_LBOUND.floatValue() - DATA_RANGE / 3);
        }

        yAxis.setUpperBound(Y_AXIS_UBOUND.floatValue() + DATA_RANGE / 3);
        yAxis.setTickUnit(DATA_RANGE / 10);


        xAxis.setTickUnit(4);
        xAxis.setAutoRanging(false);

        // Stock charts axis tick labels in menu should be invisisble
        // on black background
        xAxis.setTickLabelFill(Color.BLACK);
        yAxis.setTickLabelFill(Color.BLACK);

        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setTitle(ChartName);
        lineChart.setId("chart");
        lineChart.setLegendVisible(false);
        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < MainDataSet.size(); i++) {
            series.getData().add(new XYChart.Data(i, MainDataSet.get(i)));
        }

        lineChart.getData().add(series);

        // Configure style
        lineChart.setId("stock-chart");
        lineChart.setCreateSymbols(false); // hide dots on line
        //lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);

        return lineChart;

    }

}