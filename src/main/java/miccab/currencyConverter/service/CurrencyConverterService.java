package miccab.currencyConverter.service;

import miccab.currencyConverter.dao.CurrencyConversion;
import miccab.currencyConverter.dao.CurrencyConverterDbService;
import miccab.currencyConverter.dto.CurrencyConverterRequest;
import miccab.currencyConverter.dto.CurrencyConverterResponse;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Optional;

/**
 * Created by michal on 23.09.15.
 */
@Service
public class CurrencyConverterService {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyConverterService.class);
    private LatestExchangeRateProvider latestExchangeRateProvider;
    private CurrencyConverterDbService currencyConverterDbService;

    @Autowired
    public void setCurrencyConverterDbService(CurrencyConverterDbService currencyConverterDbService) {
        this.currencyConverterDbService = currencyConverterDbService;
    }

    @Autowired
    @Qualifier("exposedToClient")
    public void setLatestExchangeRateProvider(LatestExchangeRateProvider latestExchangeRateProvider) {
        this.latestExchangeRateProvider = latestExchangeRateProvider;
    }

    public Observable<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest request, String currentUser) {
        // TODO: support historical rates
        final Observable<Optional<LatestExchangeRateResponse>> latestExchangeRate = latestExchangeRateProvider.getLatestExchangeRate(request.toLatestExchangeRequest());
        final Observable<CurrencyConverterResponse> responseToBePersisted = latestExchangeRate.map(latestExchangeRateResponse -> CurrencyConverterResponse.fromLatestExchangeResponse(request, latestExchangeRateResponse));
        return responseToBePersisted.doOnNext(currencyConverterResponse -> persist(currentUser, currencyConverterResponse));
    }

    private CurrencyConverterResponse persist(String currentUser, CurrencyConverterResponse responseToBePersisted) {
        if (responseToBePersisted.isExchangeRateNotFound()) {
            LOG.debug("Not persisted because exchange rate not found. User={}", currentUser);
        } else {
            final CurrencyConversion currencyConversion = createCurrencyConversion(currentUser, responseToBePersisted);
            final Observable<Long> savingResult = currencyConverterDbService.save(currencyConversion);
            // persist can be done async - so updates in DB will be eventually consistent Or we may even loose data
            savingResult.forEach(persistedId -> LOG.debug("Currency conversion persisted under id={} for user={}", persistedId, currentUser),
                                 errorDuringPersistance -> LOG.error(String.format("Error reported while persisting conversion for user=%s", currentUser), errorDuringPersistance));
        }
        return responseToBePersisted;
    }

    private CurrencyConversion createCurrencyConversion(String currentUser, CurrencyConverterResponse responseToBePersisted) {
        final CurrencyConversion result = new CurrencyConversion();
        result.setCurrencyFrom(responseToBePersisted.getCurrencyFrom());
        result.setCurrencyTo(responseToBePersisted.getCurrencyTo());
        result.setExchangeRate(responseToBePersisted.getExchangeRate());
        result.setUser(currentUser);
        return result;
    }
}
