package miccab.currencyConverter.exchangeRate.api;

/**
 * Created by michal on 17.09.15.
 */
public class LatestExchangeRateRequest {
    private final String currencyFrom;
    private final String currencyTo;

    public LatestExchangeRateRequest(String currencyFrom, String currencyTo) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    @Override
    public String toString() {
        return "LatestExchangeRateRequest{" +
                "currencyFrom='" + currencyFrom + '\'' +
                ", currencyTo='" + currencyTo + '\'' +
                '}';
    }
}
