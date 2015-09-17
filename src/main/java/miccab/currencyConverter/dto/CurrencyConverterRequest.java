package miccab.currencyConverter.dto;

import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;

/**
 * Created by michal on 17.09.15.
 */
public class CurrencyConverterRequest {
    private String currencyFrom;
    private String currencyTo;

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public LatestExchangeRateRequest toLatestExchangeRequest() {
        return new LatestExchangeRateRequest(currencyFrom, currencyTo);
    }
}
