package miccab.currencyConverter.exchangeRate;

import miccab.currencyConverter.exchangeRate.impl.RateUtil;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static miccab.currencyConverter.exchangeRate.impl.RateUtil.inverse;

/**
 * Created by michal on 21.09.15.
 */
public class RateUtilInverseTest {
    @Test
    public void shouldReturnNullWhenNullInput() {
        assertNull(inverse(null));
    }

    @Test
    public void shouldInverseWhenInversionIsTerminable() {
        assertTrue(new BigDecimal("0.5").compareTo(inverse(new BigDecimal(2))) == 0);
    }

    @Test
    public void shouldInverseWhenInversionIsNotTerminable() {
        assertTrue(new BigDecimal("0.3333333333").compareTo(inverse(new BigDecimal(3))) == 0);
    }

    @Test
    public void shouldReturnZeroWhenInputIsZero() {
        assertEquals(BigDecimal.ZERO, inverse(BigDecimal.ZERO));
    }
}
