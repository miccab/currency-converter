package miccab.currencyConverter.dto;

import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by michal on 17.09.15.
 */
public class CurrencyConverterResponse {
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal exchangeRate;
    private boolean exchangeRateNotFound;

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

    public boolean isExchangeRateNotFound() {
        return exchangeRateNotFound;
    }

    public void setExchangeRateNotFound(boolean exchangeRateNotFound) {
        this.exchangeRateNotFound = exchangeRateNotFound;
    }

    public static CurrencyConverterResponse fromLatestExchangeResponse(CurrencyConverterRequest request, Optional<LatestExchangeRateResponse> latestExchangeRateResponse) {
        final CurrencyConverterResponse result = new CurrencyConverterResponse();
        result.setCurrencyFrom(request.getCurrencyFrom());
        result.setCurrencyTo(request.getCurrencyTo());
        if (latestExchangeRateResponse.isPresent()) {
            final LatestExchangeRateResponse latestExchangeRate = latestExchangeRateResponse.get();
            result.setExchangeRate(latestExchangeRate.getExchangeRate());
        } else {
            result.setExchangeRateNotFound(true);
        }
        return result;
    }
}
