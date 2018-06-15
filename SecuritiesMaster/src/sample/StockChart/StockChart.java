package sample.StockChart;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public class StockChart {

    ArrayList<Number> MainDataSet;
    protected Number Y_AXIS_UBOUND;
    protected Number Y_AXIS_LBOUND;
    protected float DATA_RANGE;
    public String ChartName;


    protected String XAxisLabel;
    protected String YAxisLabel;

    protected String MeasurementType;

    public StockChart(ArrayList<Number> dataSet) {
        MainDataSet = dataSet;

        Y_AXIS_UBOUND = getDataSetMax(dataSet);
        Y_AXIS_LBOUND = getDataSetMin(dataSet);
        DATA_RANGE = Y_AXIS_UBOUND.floatValue() - Y_AXIS_LBOUND.floatValue();

    }

    public StockChart() { /* Default constructor */ }

    protected Number getDataSetMax(ArrayList<Number> dataSet) {

        Number result;
        result = Collections.max(dataSet, null);

        return result;
    }

    public String getChartName(){
        return ChartName;
    }

    public String getXAxisLabel() {
        return XAxisLabel;
    }

    public String getYAxisLabel() {
        return YAxisLabel;
    }

    public void setXAxisLabel(String XAxisLabel) {
        this.XAxisLabel = XAxisLabel;
    }

    public void setYAxisLabel(String YAxisLabel) {
        this.YAxisLabel = YAxisLabel;
    }

    public String getMeasurementType() {
        return MeasurementType;
    }

    public void setMeasurementType(String measurementType) {
        MeasurementType = measurementType;
    }

    public void setChartName(String newChartName) {
        this.ChartName = newChartName;
    }

    protected Number getDataSetMin(ArrayList<Number> dataSet) {
        Number result;
        result = Collections.min(dataSet, null);

        return result;
    }

}
