package sample.AlphaVantage;

public class AlphaVantageQueryIntraDay extends AlphaVantageQuery {

    public AlphaVantageQueryIntraDay (String symbol, String apiKey, String interval) {
        /*
        * Valid intervals: 1min, 5min, 15min, 30min, 60min
        * */

        // TODO implement outputsize option, otherwise only gets latest 100 values
        super("TIME_SERIES_INTRADAY", symbol, apiKey);

        QueryParts.add(2,
                "&interval=");
        QueryInputs.add(2, interval);
        intervalString = interval;
        String query = buildQuery();
        System.out.println(query);

    }

}
