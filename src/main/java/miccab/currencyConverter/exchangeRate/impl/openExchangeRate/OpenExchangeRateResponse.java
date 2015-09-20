package miccab.currencyConverter.exchangeRate.impl.openExchangeRate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by michal on 18.09.15.
 */
public class OpenExchangeRateResponse {
    private String disclaimer;
    private String license;
    @JsonProperty(value = "timestamp", required = true)
    private long calculatedAtTimestamp;
    @JsonProperty(value = "base", required = true)
    private String exchangeRatesBaseCurrency;
    @JsonProperty(value = "rates", required = true)
    private Map<String, BigDecimal> exchangeRates;

    public Map<String, BigDecimal> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(Map<String, BigDecimal> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public long getCalculatedAtTimestamp() {
        return calculatedAtTimestamp;
    }

    public void setCalculatedAtTimestamp(long calculatedAtTimestamp) {
        this.calculatedAtTimestamp = calculatedAtTimestamp;
    }

    public String getExchangeRatesBaseCurrency() {
        return exchangeRatesBaseCurrency;
    }

    public void setExchangeRatesBaseCurrency(String exchangeRatesBaseCurrency) {
        this.exchangeRatesBaseCurrency = exchangeRatesBaseCurrency;
    }
}
