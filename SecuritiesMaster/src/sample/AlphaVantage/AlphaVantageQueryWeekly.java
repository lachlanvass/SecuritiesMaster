package sample.AlphaVantage;

public class AlphaVantageQueryWeekly extends AlphaVantageQuery {

    public AlphaVantageQueryWeekly(String symbol, String apikey) {

        super("TIME_SERIES_WEEKLY", symbol, apikey);
        intervalString = "Weekly";
    }
}
