package miccab.currencyConverter.exchangeRate.impl;

import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by michal on 22.09.15.
 */
@Configuration
public class TunedLatestExchangeRateProviderFactory {

    private LatestExchangeRateProvider latestExchangeRateProvider;
    private boolean cachingEnabled;
    private long cacheMaxSize;
    private long cacheExpirationTimeInMillis;

    @Value("${tunedLatestExchangeRateProvider.cache.enabled}")
    public void setCachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
    }

    @Value("${tunedLatestExchangeRateProvider.cache.maxSize}")
    public void setCacheMaxSize(long cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    @Value("${tunedLatestExchangeRateProvider.cache.expirationTimeInMillis}")
    public void setCacheExpirationTimeInMillis(long cacheExpirationTimeInMillis) {
        this.cacheExpirationTimeInMillis = cacheExpirationTimeInMillis;
    }

    @Autowired
    public void setLatestExchangeRateProvider(LatestExchangeRateProvider latestExchangeRateProvider) {
        this.latestExchangeRateProvider = latestExchangeRateProvider;
    }

    @Bean
    @Qualifier("exposedToClient")
    public LatestExchangeRateProvider tunedLatestExchangeRateProvider() {
        if (cachingEnabled) {
            return CachingLatestExchangeRateProvider.create(latestExchangeRateProvider, cacheExpirationTimeInMillis, cacheMaxSize);
        } else {
            return latestExchangeRateProvider;
        }
    }
}
