package miccab.currencyConverter.dto;

import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;

import java.math.BigDecimal;

/**
 * Created by michal on 17.09.15.
 */
public class CurrencyConverterResponse {
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal exchangeRate;

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

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public static CurrencyConverterResponse fromLatestExchangeResponse(LatestExchangeRateResponse latestExchangeRateResponse) {
        final CurrencyConverterResponse result = new CurrencyConverterResponse();
        result.setCurrencyFrom(latestExchangeRateResponse.getCurrencyFrom());
        result.setCurrencyTo(latestExchangeRateResponse.getCurrencyTo());
        result.setExchangeRate(latestExchangeRateResponse.getExchangeRate());
        return result;
    }
}
