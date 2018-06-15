package sample.AlphaVantage;

public class AlphaVantageQueryDailyAdjusted extends AlphaVantageQuery {

    public AlphaVantageQueryDailyAdjusted(String symbol, String apikey) {
        super ("TIME_SERIES_DAILY_ADJUSTED", symbol, apikey);


    }
}