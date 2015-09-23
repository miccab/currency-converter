package miccab.currencyConverter.exchangeRate.impl;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import rx.Observable;

import java.util.Optional;

/**
 * Created by michal on 22.09.15.
 */
public class HystrixLatestExchangeRateProvider implements LatestExchangeRateProvider {

    private final LatestExchangeRateProvider latestExchangeRateProvider;
    private final int maxConcurrentRequests;

    public HystrixLatestExchangeRateProvider(LatestExchangeRateProvider latestExchangeRateProvider, int maxConcurrentRequests) {
        this.latestExchangeRateProvider = latestExchangeRateProvider;
        this.maxConcurrentRequests = maxConcurrentRequests;
    }

    @Override
    public Observable<Optional<LatestExchangeRateResponse>> getLatestExchangeRate(LatestExchangeRateRequest request) {
        return new LatestExchangeRateCommand(request).observe();
    }

    class LatestExchangeRateCommand extends HystrixObservableCommand<Optional<LatestExchangeRateResponse>> {

        private final LatestExchangeRateRequest request;

        public LatestExchangeRateCommand(LatestExchangeRateRequest request) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("LatestExchangeRateProvider"))
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                                                 .withExecutionIsolationSemaphoreMaxConcurrentRequests(maxConcurrentRequests)
                                                                 .withExecutionTimeoutEnabled(false)));
            this.request = request;
        }

        @Override
        protected Observable<Optional<LatestExchangeRateResponse>> construct() {
            return HystrixLatestExchangeRateProvider.this.latestExchangeRateProvider.getLatestExchangeRate(request);
        }
    }

}

