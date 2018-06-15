package sample.AlphaVantage;

public class AlphaVantageQueryMonthlyAdjusted extends AlphaVantageQuery {

    public AlphaVantageQueryMonthlyAdjusted(String symbol, String apikey) {
        super("TIME_SERIES_MONTHLY_ADJUSTED", symbol, apikey);

    }
}