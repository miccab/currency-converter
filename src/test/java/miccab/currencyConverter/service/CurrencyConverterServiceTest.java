package miccab.currencyConverter.service;

import miccab.currencyConverter.dao.CurrencyConversion;
import miccab.currencyConverter.dao.CurrencyConverterDbService;
import miccab.currencyConverter.dto.CurrencyConverterRequest;
import miccab.currencyConverter.dto.CurrencyConverterResponse;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by michal on 23.09.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencyConverterServiceTest {

    @Mock
    LatestExchangeRateProvider latestExchangeRateProvider;
    @Mock
    CurrencyConverterDbService currencyConverterDbService;
    @InjectMocks
    CurrencyConverterService currencyConverterService;

    @Test
    public void shouldReturnLatestExchangeDataWithErrorWhenExchangeRateDidNotFindRate() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        when(latestExchangeRateProvider.getLatestExchangeRate(any(LatestExchangeRateRequest.class))).thenReturn(Observable.just(Optional.empty()));

        Observable<CurrencyConverterResponse> observableResult = currencyConverterService.convertCurrency(request, "user");

        CurrencyConverterResponse result = observableResult.toBlocking().single();
        assertEquals(request.getCurrencyFrom(), result.getCurrencyFrom());
        assertEquals(request.getCurrencyTo(), result.getCurrencyTo());
        assertEquals(true, result.isExchangeRateNotFound());
    }

    @Test
    public void shouldReturnLatestExchangeDataWhenExchangeRateIsFound() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        LatestExchangeRateResponse response = new LatestExchangeRateResponse(request.getCurrencyFrom(), request.getCurrencyTo(), BigDecimal.TEN, LocalDateTime.now());
        when(latestExchangeRateProvider.getLatestExchangeRate(any(LatestExchangeRateRequest.class))).thenReturn(Observable.just(Optional.of(response)));
        when(currencyConverterDbService.save(any(CurrencyConversion.class))).thenReturn(Observable.just(Long.valueOf(1)));

        Observable<CurrencyConverterResponse> observableResult = currencyConverterService.convertCurrency(request, "user");

        CurrencyConverterResponse result = observableResult.toBlocking().single();
        assertEquals(request.getCurrencyFrom(), result.getCurrencyFrom());
        assertEquals(request.getCurrencyTo(), result.getCurrencyTo());
        assertFalse(result.isExchangeRateNotFound());
        assertEquals(BigDecimal.TEN, result.getExchangeRate());
    }


}
