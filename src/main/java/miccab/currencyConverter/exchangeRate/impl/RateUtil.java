package miccab.currencyConverter.exchangeRate.impl;

import java.math.BigDecimal;

/**
 * Created by michal on 21.09.15.
 */
public class RateUtil {
    public static BigDecimal inverse(BigDecimal rate) {
        if (rate == null) {
            return null;
        }
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ONE.divide(rate, 10, BigDecimal.ROUND_HALF_UP);
    }
}
