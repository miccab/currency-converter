package miccab.currencyConverter.service;

import miccab.currencyConverter.dao.ConversionType;
import miccab.currencyConverter.dao.CurrencyConversion;
import miccab.currencyConverter.dao.CurrencyConverterDbService;
import miccab.currencyConverter.dto.CurrencyConversionsResponse;
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
import java.util.Arrays;
import java.util.Iterator;
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
    public void convertCurrency_shouldReturnLatestExchangeDataWithErrorWhenExchangeRateDidNotFindRate() {
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
    public void convertCurrency_shouldReturnLatestExchangeDataWhenExchangeRateIsFound() {
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

    @Test
    public void convertCurrency_shouldReturnLatestExchangeDataWhenExchangeRateIsFoundButSavingInDBFailed() {
        final CurrencyConverterRequest request = new CurrencyConverterRequest();
        request.setCurrencyFrom("EUR");
        request.setCurrencyTo("USD");
        LatestExchangeRateResponse response = new LatestExchangeRateResponse(request.getCurrencyFrom(), request.getCurrencyTo(), BigDecimal.TEN, LocalDateTime.now());
        when(latestExchangeRateProvider.getLatestExchangeRate(any(LatestExchangeRateRequest.class))).thenReturn(Observable.just(Optional.of(response)));
        when(currencyConverterDbService.save(any(CurrencyConversion.class))).thenReturn(Observable.error(new RuntimeException("DB error")));

        Observable<CurrencyConverterResponse> observableResult = currencyConverterService.convertCurrency(request, "user");

        CurrencyConverterResponse result = observableResult.toBlocking().single();
        assertEquals(request.getCurrencyFrom(), result.getCurrencyFrom());
        assertEquals(request.getCurrencyTo(), result.getCurrencyTo());
        assertFalse(result.isExchangeRateNotFound());
        assertEquals(BigDecimal.TEN, result.getExchangeRate());
    }

    @Test
    public void getRecentConversionsForUser_shouldReturnConversionsWhenDbHasAny() {

        CurrencyConversion currencyConversionFirst = new CurrencyConversion();
        currencyConversionFirst.setCalculatedAtTime(LocalDateTime.MAX);
        currencyConversionFirst.setConversionType(ConversionType.LATEST);
        currencyConversionFirst.setCurrencyFrom("A");
        currencyConversionFirst.setCurrencyTo("B");
        currencyConversionFirst.setExchangeRate(BigDecimal.TEN);
        currencyConversionFirst.setInsertTime(LocalDateTime.MIN);
        currencyConversionFirst.setUser("userX");
        CurrencyConversion currencyConversionSecond = new CurrencyConversion();
        currencyConversionSecond.setCalculatedAtTime(LocalDateTime.MIN);
        currencyConversionSecond.setConversionType(ConversionType.HIST);
        currencyConversionSecond.setCurrencyFrom("B");
        currencyConversionSecond.setCurrencyTo("A");
        currencyConversionSecond.setExchangeRate(BigDecimal.ONE);
        currencyConversionSecond.setInsertTime(LocalDateTime.MAX);
        currencyConversionSecond.setUser("userY");
        when(currencyConverterDbService.getRecentConversionsForUser("user")).thenReturn(Observable.just(Arrays.asList(currencyConversionFirst, currencyConversionSecond)));

        Observable<CurrencyConversionsResponse> observableResult = currencyConverterService.getRecentConversionsForUser("user");

        CurrencyConversionsResponse result = observableResult.toBlocking().single();
        assertEquals(2, result.getCurrencyConversions().size());
        Iterator<miccab.currencyConverter.dto.CurrencyConversion> iterator = result.getCurrencyConversions().iterator();
        {
            miccab.currencyConverter.dto.CurrencyConversion record = iterator.next();
            assertEquals("A", record.getCurrencyFrom());
            assertEquals("B", record.getCurrencyTo());
            assertEquals("userX", record.getUser());
            assertEquals(LocalDateTime.MAX, record.getCalculatedAtTime());
            assertEquals(ConversionType.LATEST, record.getConversionType());
            assertEquals(BigDecimal.TEN, record.getExchangeRate());
            assertEquals(LocalDateTime.MIN, record.getInsertTime());
        }
        {
            miccab.currencyConverter.dto.CurrencyConversion record = iterator.next();
            assertEquals("B", record.getCurrencyFrom());
            assertEquals("A", record.getCurrencyTo());
            assertEquals("userY", record.getUser());
            assertEquals(LocalDateTime.MIN, record.getCalculatedAtTime());
            assertEquals(ConversionType.HIST, record.getConversionType());
            assertEquals(BigDecimal.ONE, record.getExchangeRate());
            assertEquals(LocalDateTime.MAX, record.getInsertTime());
        }
    }

}
