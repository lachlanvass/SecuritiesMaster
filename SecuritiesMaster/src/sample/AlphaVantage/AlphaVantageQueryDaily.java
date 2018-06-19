package sample.AlphaVantage;

public class AlphaVantageQueryDaily extends AlphaVantageQuery {

    public AlphaVantageQueryDaily(String symbol, String apikey) {
        super ("TIME_SERIES_DAILY", symbol, apikey);
        intervalString = "Daily";


    }
}
