package miccab.currencyConverter.exchangeRate.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by michal on 22.09.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CachingLatestExchangeRateProviderTest {

    @Mock
    LatestExchangeRateProvider latestExchangeRateProvider;

    CachingLatestExchangeRateProvider cachingLatestExchangeRateProvider;

    @Before
    public void setUp() {
        Cache<CacheKey, Optional<LatestExchangeRateResponse>> cache = CacheBuilder.newBuilder().build();
        cachingLatestExchangeRateProvider = new CachingLatestExchangeRateProvider(latestExchangeRateProvider, cache);
    }

    @Test
    public void shouldReturnFromCacheWhenCalledSecondTime() {
        LatestExchangeRateRequest request = new LatestExchangeRateRequest("USD", "EUR");
        when(latestExchangeRateProvider.getLatestExchangeRate(request)).thenReturn(
                Observable.just(Optional.of(new LatestExchangeRateResponse("USD", "EUR", BigDecimal.TEN, LocalDateTime.MAX))));
        {
            Observable<Optional<LatestExchangeRateResponse>> observableResult = cachingLatestExchangeRateProvider.getLatestExchangeRate(request);
            LatestExchangeRateResponse result = observableResult.toBlocking().single().get();
            assertEquals(BigDecimal.TEN, result.getExchangeRate());
            assertEquals(LocalDateTime.MAX, result.getCalculatedAt());
        }
        {
            Observable<Optional<LatestExchangeRateResponse>> observableResult = cachingLatestExchangeRateProvider.getLatestExchangeRate(request);
            LatestExchangeRateResponse result = observableResult.toBlocking().single().get();
            assertEquals(BigDecimal.TEN, result.getExchangeRate());
            assertEquals(LocalDateTime.MAX, result.getCalculatedAt());
        }
        verify(latestExchangeRateProvider, times(1)).getLatestExchangeRate(request);
    }
}
