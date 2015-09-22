package miccab.currencyConverter.exchangeRate.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import rx.Observable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by michal on 22.09.15.
 */
public class CachingLatestExchangeRateProvider implements LatestExchangeRateProvider {

    private final LatestExchangeRateProvider latestExchangeRateProvider;
    private final Cache<CacheKey, Optional<LatestExchangeRateResponse>> cache;

    public CachingLatestExchangeRateProvider(LatestExchangeRateProvider latestExchangeRateProvider, Cache<CacheKey, Optional<LatestExchangeRateResponse>> cache) {
        this.latestExchangeRateProvider = latestExchangeRateProvider;
        this.cache = cache;
    }

    public static CachingLatestExchangeRateProvider create(LatestExchangeRateProvider latestExchangeRateProvider, long expirationTimeInMillis, long maxSize) {
        Cache<CacheKey, Optional<LatestExchangeRateResponse>> guavaCache = CacheBuilder.newBuilder()
                .expireAfterWrite(expirationTimeInMillis, TimeUnit.MILLISECONDS)
                .maximumSize(maxSize)
                .build();
        return new CachingLatestExchangeRateProvider(latestExchangeRateProvider, guavaCache);
    }

    @Override
    public Observable<Optional<LatestExchangeRateResponse>> getLatestExchangeRate(LatestExchangeRateRequest request) {
        final CacheKey cacheKey = CacheKey.create(request);
        final Optional<LatestExchangeRateResponse> resultFromCache = cache.getIfPresent(cacheKey);
        if (resultFromCache != null) {
            return Observable.just(resultFromCache);
        } else {
            final Observable<Optional<LatestExchangeRateResponse>> resultFromRealService = latestExchangeRateProvider.getLatestExchangeRate(request);
            resultFromRealService.forEach(exchangeRate -> cache.put(cacheKey, exchangeRate), this::onError);
            return resultFromRealService;
        }
    }

    private void onError(Throwable throwable) {
        // ignore error here
    }
}

class CacheKey {
    private final String currencyFrom;
    private final String currencyTo;

    public CacheKey(String currencyFrom, String currencyTo) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        CacheKey rhs = (CacheKey) obj;
        return new EqualsBuilder()
                .append(currencyFrom, rhs.currencyFrom)
                .append(currencyTo, rhs.currencyTo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(currencyFrom).
                append(currencyTo).
                toHashCode();
    }

    public static CacheKey create(LatestExchangeRateRequest request) {
        return new CacheKey(request.getCurrencyFrom(), request.getCurrencyTo());
    }
}
