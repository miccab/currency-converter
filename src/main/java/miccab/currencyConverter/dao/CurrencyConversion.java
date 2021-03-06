package miccab.currencyConverter.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by michal on 23.09.15.
 */
@Entity
public class CurrencyConversion {
    @Id
    @GeneratedValue
    private Long id;
    private String currencyFrom;
    private String currencyTo;
    @Column(scale = 10, precision = 10)
    private BigDecimal exchangeRate;
    private String user;
    private LocalDateTime insertTime;
    private ConversionType conversionType;
    private LocalDateTime calculatedAtTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
