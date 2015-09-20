package miccab.currencyConverter.exchangeRate.impl.openExchangeRate;

import miccab.currencyConverter.Profiles;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateRequest;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Created by michal on 18.09.15.
 */
@Service
@Profile(Profiles.EXTERNAL_SERVICES_REAL)
public class OpenExchangeRateLatestProvider implements LatestExchangeRateProvider {

    private OpenExchangeRateLatestClient client;

    @Autowired
    public void setClient(OpenExchangeRateLatestClient client) {
        this.client = client;
    }

    @Override
    public Observable<LatestExchangeRateResponse> getLatestExchangeRate(LatestExchangeRateRequest request) {
        final Observable<OpenExchangeRateResponse> observableResponse = client.getLatestExchangeRate();
        return observableResponse.map(response -> convertOpenExchangeRateResponseToProviderFormat(request, response));
    }

    LatestExchangeRateResponse convertOpenExchangeRateResponseToProviderFormat(LatestExchangeRateRequest request, OpenExchangeRateResponse response) {
        final BigDecimal exchangeRate;
        if (Objects.equals(response.getExchangeRatesBaseCurrency(), request.getCurrencyFrom())) {
            exchangeRate = response.getExchangeRates().get(request.getCurrencyTo());
            if (exchangeRate == null) {
                throw new RuntimeException(String.format("Requested exchange rate not found for %s", request.getCurrencyTo()));
            }
        } else {
            // TODO
            exchangeRate = BigDecimal.ONE;
        }
        final LocalDateTime calculatedAt = LocalDateTime.ofEpochSecond(response.getCalculatedAtTimestamp(), 0, ZoneOffset.UTC);
        return new LatestExchangeRateResponse(request.getCurrencyFrom(), request.getCurrencyTo(), exchangeRate, calculatedAt);
    }
}
