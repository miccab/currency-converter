package miccab.currencyConverter.exchangeRate.impl;

import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by michal on 17.09.15.
 */
@Service
public class FakeLatestExchangeRateProvider implements LatestExchangeRateProvider {
    @Override
    public Observable<LatestExchangeRateResponse> getLatestExchangeRate(LatestExchangeRateRequest request) {
        return Observable.just(
                new LatestExchangeRateResponse(
                        request.getCurrencyFrom(),
                        request.getCurrencyTo(),
                        BigDecimal.ONE,
                        LocalDateTime.now())
        );
    }
}
