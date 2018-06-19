package sample.AlphaVantage;

public class AlphaVantageQueryMonthly extends AlphaVantageQuery {

    public AlphaVantageQueryMonthly(String symbol, String apikey) {
        super("TIME_SERIES_MONTHLY", symbol, apikey);
        intervalString = "Monthly";

    }
}
