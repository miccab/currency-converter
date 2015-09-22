package miccab.currencyConverter.exchangeRate.api;

import rx.Observable;

import java.util.Optional;

/**
 * Created by michal on 17.09.15.
 */
public interface LatestExchangeRateProvider {
    Observable<Optional<LatestExchangeRateResponse>> getLatestExchangeRate(LatestExchangeRateRequest request);
}
