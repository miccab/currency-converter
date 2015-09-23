package miccab.currencyConverter.dto;

import java.util.List;

/**
 * Created by michal on 23.09.15.
 */
public class CurrencyConversionsResponse {
    private List<CurrencyConversion> currencyConversions;

    public List<CurrencyConversion> getCurrencyConversions() {
        return currencyConversions;
    }

    public void setCurrencyConversions(List<CurrencyConversion> currencyConversions) {
        this.currencyConversions = currencyConversions;
    }
}
