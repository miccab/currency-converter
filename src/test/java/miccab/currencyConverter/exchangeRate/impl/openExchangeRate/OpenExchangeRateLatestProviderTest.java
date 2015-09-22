package miccab.currencyConverter.exchangeRate.impl.openExchangeRate;

import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by michal on 20.09.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class OpenExchangeRateLatestProviderTest {

    @InjectMocks
    OpenExchangeRateLatestProvider openExchangeRateLatestProvider;
    @Mock
    OpenExchangeRateLatestClient client;

    private Map<String, BigDecimal> createRates(String currency, BigDecimal rate) {
        final Map<String, BigDecimal> result = new HashMap<>();
        result.put(currency, rate);
        return result;
    }

    private Map<String, BigDecimal> addRate(Map<String, BigDecimal> rates, String currency, BigDecimal rate) {
        rates.put(currency, rate);
        return rates;
    }

    private Observable<OpenExchangeRateResponse> createClientResponse(String base, Map<String, BigDecimal> rates) {
        final OpenExchangeRateResponse response = new OpenExchangeRateResponse();
        response.setExchangeRatesBaseCurrency(base);
        response.setExchangeRates(rates);
        return Observable.just(response);
    }

    @Test
    public void shouldNotCallExternalSystemWhenCurrencyFromSameAsCurrencyTo() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("USD", "USD");

        Observable<Optional<LatestExchangeRateResponse>> observableResult = openExchangeRateLatestProvider.getLatestExchangeRate(request);

        LatestExchangeRateResponse result = observableResult.toBlocking().single().get();
        assertEquals("USD", result.getCurrencyFrom());
        assertEquals("USD", result.getCurrencyTo());
        assertEquals(BigDecimal.ONE, result.getExchangeRate());
        assertNotNull(result.getCalculatedAt());

        verifyZeroInteractions(client);
    }

    @Test
    public void shouldUseExchangeRateDirectlyWhenBaseCurrencyMatchesCurrencyFrom() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("USD", "EUR");
        when(client.getLatestExchangeRate()).thenReturn(createClientResponse("USD", createRates("EUR", BigDecimal.TEN)));

        Observable<Optional<LatestExchangeRateResponse>> observableResult = openExchangeRateLatestProvider.getLatestExchangeRate(request);

        LatestExchangeRateResponse result = observableResult.toBlocking().single().get();
        assertEquals("USD", result.getCurrencyFrom());
        assertEquals("EUR", result.getCurrencyTo());
        assertEquals(BigDecimal.TEN, result.getExchangeRate());
        assertNotNull(result.getCalculatedAt());
    }

    @Test
    public void shouldUseInverseExchangeRateWhenBaseCurrencyMatchesCurrencyTo() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("EUR", "USD");
        when(client.getLatestExchangeRate()).thenReturn(createClientResponse("USD", createRates("EUR", BigDecimal.TEN)));

        Observable<Optional<LatestExchangeRateResponse>> observableResult = openExchangeRateLatestProvider.getLatestExchangeRate(request);

        LatestExchangeRateResponse result = observableResult.toBlocking().single().get();
        assertEquals("EUR", result.getCurrencyFrom());
        assertEquals("USD", result.getCurrencyTo());
        assertTrue(result.getExchangeRate().toString(), new BigDecimal("0.1").compareTo(result.getExchangeRate()) == 0);
        assertNotNull(result.getCalculatedAt());
    }

    @Test
    public void shouldComputeExchangeRateWhenBaseCurrencyIsNotEqualCurrencyFromAndTo() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("EUR", "AED");
        when(client.getLatestExchangeRate()).thenReturn(createClientResponse("USD", addRate(createRates("EUR", new BigDecimal("2")), "AED", new BigDecimal("0.5"))));

        Observable<Optional<LatestExchangeRateResponse>> observableResult = openExchangeRateLatestProvider.getLatestExchangeRate(request);

        LatestExchangeRateResponse result = observableResult.toBlocking().single().get();
        assertEquals("EUR", result.getCurrencyFrom());
        assertEquals("AED", result.getCurrencyTo());
        assertTrue(result.getExchangeRate().toString(), new BigDecimal("0.25").compareTo(result.getExchangeRate()) == 0);
        assertNotNull(result.getCalculatedAt());
    }

    @Test
    public void shouldReturnEmptyResultWhenCurrencyHasNoExchangeRate() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("EUR", "USD");
        when(client.getLatestExchangeRate()).thenReturn(createClientResponse("USD", new HashMap<>()));

        Observable<Optional<LatestExchangeRateResponse>> observableResult = openExchangeRateLatestProvider.getLatestExchangeRate(request);

        Optional<LatestExchangeRateResponse> result = observableResult.toBlocking().single();
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldReportErrorWhenUnexpectedErrorHappens() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("USD", "EUR");
        when(client.getLatestExchangeRate()).thenReturn(Observable.error(new RuntimeException("error")));

        Observable<Optional<LatestExchangeRateResponse>> observableResult = openExchangeRateLatestProvider.getLatestExchangeRate(request);

        try {
            observableResult.toBlocking().single();
            fail();
        } catch (RuntimeException e) {
            assertEquals("error", e.getMessage());
        }
    }
}
