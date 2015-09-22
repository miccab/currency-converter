package miccab.currencyConverter.exchangeRate.impl.openExchangeRate;

import miccab.currencyConverter.Profiles;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static miccab.currencyConverter.exchangeRate.impl.RateUtil.inverse;

/**
 * Created by michal on 18.09.15.
 */
@Service
@Profile(Profiles.EXTERNAL_SERVICES_REAL)
public class OpenExchangeRateLatestProvider implements LatestExchangeRateProvider {
    private static final Logger LOG = LoggerFactory.getLogger(OpenExchangeRateLatestProvider.class);
    private OpenExchangeRateLatestClient client;

    @Autowired
    public void setClient(OpenExchangeRateLatestClient client) {
        this.client = client;
    }

    @Override
    public Observable<Optional<LatestExchangeRateResponse>> getLatestExchangeRate(LatestExchangeRateRequest request) {
        LOG.debug("Requesting latest exchange rate. Request={}", request);
        if (request.getCurrencyFrom().equals(request.getCurrencyTo())) {
            LOG.debug("Currencies are equal. Returning 1 as exchange rate.");
            final LatestExchangeRateResponse response = new LatestExchangeRateResponse(request.getCurrencyFrom(), request.getCurrencyTo(), BigDecimal.ONE, LocalDateTime.now());
            return Observable.just(Optional.of(response));
        } else {
            final Observable<OpenExchangeRateResponse> observableResponse = client.getLatestExchangeRate();
            return observableResponse.map(response -> convertOpenExchangeRateResponseToProviderFormat(request, response));
        }
    }

    private Optional<LatestExchangeRateResponse> convertOpenExchangeRateResponseToProviderFormat(LatestExchangeRateRequest request, OpenExchangeRateResponse response) {
        final BigDecimal exchangeRate;
        if (request.getCurrencyFrom().equals(response.getExchangeRatesBaseCurrency())) {
            exchangeRate = response.getExchangeRates().get(request.getCurrencyTo());
        } else if (request.getCurrencyTo().equals(response.getExchangeRatesBaseCurrency())) {
            exchangeRate = inverse(response.getExchangeRates().get(request.getCurrencyFrom()));
            // TODO: control precision
        } else {
            exchangeRate = computeExchangeRateUsingBaseCurrency(request, response);
        }
        if (exchangeRate == null) {
            LOG.info("Exchange rate(s) not found in external system. Request={}", request);
            return Optional.empty();
        } else {
            final LocalDateTime calculatedAt = LocalDateTime.ofEpochSecond(response.getCalculatedAtTimestamp(), 0, ZoneOffset.UTC);
            return Optional.of(new LatestExchangeRateResponse(request.getCurrencyFrom(), request.getCurrencyTo(), exchangeRate, calculatedAt));
        }
    }

    private BigDecimal computeExchangeRateUsingBaseCurrency(LatestExchangeRateRequest request, OpenExchangeRateResponse response) {
        final BigDecimal rateFromBase2CurrencyFrom = response.getExchangeRates().get(request.getCurrencyFrom());
        final BigDecimal rateFromBase2CurrencyTo = response.getExchangeRates().get(request.getCurrencyTo());
        if (rateFromBase2CurrencyFrom == null || rateFromBase2CurrencyTo == null) {
            return null;
        } else {
            final BigDecimal rateFromCurrencyFrom2Base = inverse(rateFromBase2CurrencyFrom);
            return rateFromCurrencyFrom2Base.multiply(rateFromBase2CurrencyTo);
            // TODO: control precision
        }
    }
}
