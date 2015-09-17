package miccab.currencyConverter.exchangeRate.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by michal on 17.09.15.
 */
public class LatestExchangeRateResponse {
    private final String currencyFrom;
    private final String currencyTo;
    private final BigDecimal exchangeRate;
    private final LocalDateTime calculatedAt;

    public LatestExchangeRateResponse(String currencyFrom, String currencyTo, BigDecimal exchangeRate, LocalDateTime calculatedAt) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.exchangeRate = exchangeRate;
        this.calculatedAt = calculatedAt;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
}
