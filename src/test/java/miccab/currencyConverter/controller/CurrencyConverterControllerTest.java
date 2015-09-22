package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by michal on 17.09.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencyConverterControllerTest {

    @Mock
    LatestExchangeRateProvider latestExchangeRateProvider;
    CurrencyConverterController currencyConverterController;

    @Before
    public void setUp() {
        currencyConverterController = new CurrencyConverterController();
        currencyConverterController.setLatestExchangeRateProvider(latestExchangeRateProvider);
        currencyConverterController.setDeferredResultFactory(new DeferredResultFactory());
    }


    @Test
    public void shouldReturnExceptionWhenExchangeRateReportedUnexpectedError() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        when(latestExchangeRateProvider.getLatestExchangeRate(any(LatestExchangeRateRequest.class))).thenReturn(Observable.error(new RuntimeException("Unexpected error")));

        DeferredResult<CurrencyConverterResponse> result = currencyConverterController.convertCurrency(request);

        assertTrue(result.hasResult());
        assertTrue(result.getResult() instanceof RuntimeException);

    }

    @Test
    public void shouldReturnLatestExchangeDataWithErrorWhenExchangeRateDidNotFindRate() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        when(latestExchangeRateProvider.getLatestExchangeRate(any(LatestExchangeRateRequest.class))).thenReturn(Observable.just(Optional.empty()));

        DeferredResult<CurrencyConverterResponse> result = currencyConverterController.convertCurrency(request);

        assertTrue(result.hasResult());
        final CurrencyConverterResponse converterResult = (CurrencyConverterResponse) result.getResult();
        assertEquals(request.getCurrencyFrom(), converterResult.getCurrencyFrom());
        assertEquals(request.getCurrencyTo(), converterResult.getCurrencyTo());
        assertEquals(true, converterResult.isExchangeRateNotFound());
    }

    @Test
    public void shouldReturnLatestExchangeDataWhenExchangeRateWasSuccessful() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        final LatestExchangeRateResponse responseFromExchangeRateProvider = new LatestExchangeRateResponse(request.getCurrencyFrom(), request.getCurrencyTo(), BigDecimal.TEN, LocalDateTime.now());
        when(latestExchangeRateProvider.getLatestExchangeRate(any(LatestExchangeRateRequest.class))).thenReturn(Observable.just(Optional.of(responseFromExchangeRateProvider)));

        DeferredResult<CurrencyConverterResponse> result = currencyConverterController.convertCurrency(request);

        assertTrue(result.hasResult());
        final CurrencyConverterResponse converterResult = (CurrencyConverterResponse) result.getResult();
        assertEquals(BigDecimal.TEN, converterResult.getExchangeRate());
        assertEquals(request.getCurrencyFrom(), converterResult.getCurrencyFrom());
        assertEquals(request.getCurrencyTo(), converterResult.getCurrencyTo());
    }
}
