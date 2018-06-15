package sample.AlphaVantage;

public class AlphaVantageQueryWeeklyAdjusted extends AlphaVantageQuery {

    public AlphaVantageQueryWeeklyAdjusted(String symbol, String apikey) {

        super("TIME_SERIES_WEEKLY_ADJUSTED", symbol, apikey);

    }
}
