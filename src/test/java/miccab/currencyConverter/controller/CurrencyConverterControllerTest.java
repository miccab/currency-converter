package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.service.CurrencyConverterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by michal on 17.09.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencyConverterControllerTest {

    @Mock
    CurrencyConverterService currencyConverterService;
    CurrencyConverterController currencyConverterController;

    @Before
    public void setUp() {
        currencyConverterController = new CurrencyConverterController();
        currencyConverterController.setCurrencyConverterService(currencyConverterService);
        currencyConverterController.setDeferredResultFactory(new DeferredResultFactory());
    }


    @Test
    public void convertCurrency_shouldReturnExceptionWhenExchangeRateReportedUnexpectedError() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        when(currencyConverterService.convertCurrency(eq(request), anyString())).thenReturn(Observable.error(new RuntimeException("Unexpected error")));

        DeferredResult<CurrencyConverterResponse> result = currencyConverterController.convertCurrency(request);

        assertTrue(result.hasResult());
        assertTrue(result.getResult() instanceof RuntimeException);

    }

    @Test
    public void convertCurrency_shouldReturnLatestExchangeDataWhenExchangeRateWasSuccessful() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        final CurrencyConverterResponse responseFromExchangeRateProvider = new CurrencyConverterResponse();
        responseFromExchangeRateProvider.setCurrencyFrom(request.getCurrencyFrom());
        responseFromExchangeRateProvider.setCurrencyTo(request.getCurrencyTo());
        responseFromExchangeRateProvider.setExchangeRate(BigDecimal.TEN);
        responseFromExchangeRateProvider.setCalculatedAt(LocalDateTime.MAX);
        when(currencyConverterService.convertCurrency(eq(request), anyString())).thenReturn(Observable.just(responseFromExchangeRateProvider));

        DeferredResult<CurrencyConverterResponse> result = currencyConverterController.convertCurrency(request);

        assertTrue(result.hasResult());
        final CurrencyConverterResponse converterResult = (CurrencyConverterResponse) result.getResult();
        assertEquals(BigDecimal.TEN, converterResult.getExchangeRate());
        assertEquals(request.getCurrencyFrom(), converterResult.getCurrencyFrom());
        assertEquals(request.getCurrencyTo(), converterResult.getCurrencyTo());
        assertEquals(LocalDateTime.MAX, converterResult.getCalculatedAt());
    }

    @Test
    public void getRecentConversionsForUser_shouldReturnDataWhenRecentConversionsLookupWasSuccessful() {
        CurrencyConversionsResponse responseFromService = new CurrencyConversionsResponse();
        when(currencyConverterService.getRecentConversionsForUser(anyString())).thenReturn(Observable.just(responseFromService));

        DeferredResult<CurrencyConversionsResponse> result = currencyConverterController.getRecentConversionsForUser();

        assertTrue(result.hasResult());
        assertTrue(result.getResult() instanceof CurrencyConversionsResponse);
    }
}
