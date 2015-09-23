package miccab.currencyConverter.dto;

import miccab.currencyConverter.dao.ConversionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by michal on 23.09.15.
 */
public class CurrencyConversion {
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal exchangeRate;
    private String user;
    private LocalDateTime insertTime;
    private ConversionType conversionType;
    private LocalDateTime calculatedAtTime;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public ConversionType getConversionType() {
        return conversionType;
    }

    public void setConversionType(ConversionType conversionType) {
        this.conversionType = conversionType;
    }

    public LocalDateTime getCalculatedAtTime() {
        return calculatedAtTime;
    }

    public void setCalculatedAtTime(LocalDateTime calculatedAtTime) {
        this.calculatedAtTime = calculatedAtTime;
    }
}
