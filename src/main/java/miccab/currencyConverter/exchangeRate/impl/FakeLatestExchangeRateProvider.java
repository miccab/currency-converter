package miccab.currencyConverter.exchangeRate.impl;

import miccab.currencyConverter.Profiles;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by michal on 17.09.15.
 */
@Service
@Profile(Profiles.EXTERNAL_SERVICES_FAKE)
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
