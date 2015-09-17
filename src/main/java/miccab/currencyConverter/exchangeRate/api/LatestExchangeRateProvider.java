package miccab.currencyConverter.exchangeRate.api;

import rx.Observable;

/**
 * Created by michal on 17.09.15.
 */
public interface LatestExchangeRateProvider {
    Observable<LatestExchangeRateResponse> getLatestExchangeRate(LatestExchangeRateRequest request);
}
