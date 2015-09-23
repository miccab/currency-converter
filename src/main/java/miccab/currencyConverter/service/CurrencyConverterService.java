package miccab.currencyConverter.service;

import miccab.currencyConverter.dao.ConversionType;
import miccab.currencyConverter.dao.CurrencyConversion;
import miccab.currencyConverter.dao.CurrencyConverterDbService;
import miccab.currencyConverter.dto.CurrencyConversionsResponse;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        return responseToBePersisted.doOnNext(currencyConverterResponse -> persist(currentUser, currencyConverterResponse, ConversionType.LATEST));
    }

    private CurrencyConverterResponse persist(String currentUser, CurrencyConverterResponse responseToBePersisted, ConversionType conversionType) {
        if (responseToBePersisted.isExchangeRateNotFound()) {
            LOG.debug("Not persisted because exchange rate not found. User={}", currentUser);
        } else {
            final CurrencyConversion currencyConversion = createCurrencyConversion(currentUser, responseToBePersisted, conversionType);
            final Observable<Long> savingResult = currencyConverterDbService.save(currencyConversion);
            // persist can be done async - so updates in DB will be eventually consistent Or we may even loose data
            savingResult.forEach(persistedId -> LOG.debug("Currency conversion persisted under id={} for user={}", persistedId, currentUser),
                                 errorDuringPersistance -> LOG.error(String.format("Error reported while persisting conversion for user=%s", currentUser), errorDuringPersistance));
        }
        return responseToBePersisted;
    }

    private CurrencyConversion createCurrencyConversion(String currentUser, CurrencyConverterResponse responseToBePersisted, ConversionType conversionType) {
        final CurrencyConversion result = new CurrencyConversion();
        result.setCurrencyFrom(responseToBePersisted.getCurrencyFrom());
        result.setCurrencyTo(responseToBePersisted.getCurrencyTo());
        result.setExchangeRate(responseToBePersisted.getExchangeRate());
        result.setUser(currentUser);
        result.setConversionType(conversionType);
        result.setInsertTime(LocalDateTime.now());
        result.setCalculatedAtTime(responseToBePersisted.getCalculatedAt());
        return result;
    }

    public Observable<CurrencyConversionsResponse> getRecentConversionsForUser(String currentUser) {
        final Observable<List<CurrencyConversion>> result = currencyConverterDbService.getRecentConversionsForUser(currentUser);
        return result.map(this::convertToCurrencyConversionsResponse);
    }

    private CurrencyConversionsResponse convertToCurrencyConversionsResponse(List<CurrencyConversion> resultFromDbService) {
        final CurrencyConversionsResponse result = new CurrencyConversionsResponse();
        final List<miccab.currencyConverter.dto.CurrencyConversion> conversionList = new ArrayList<>(resultFromDbService.size());
        result.setCurrencyConversions(conversionList);
        for (CurrencyConversion daoItem : resultFromDbService) {
            final miccab.currencyConverter.dto.CurrencyConversion resultToBeTransformed = new miccab.currencyConverter.dto.CurrencyConversion();
            resultToBeTransformed.setConversionType(daoItem.getConversionType());
            resultToBeTransformed.setCurrencyFrom(daoItem.getCurrencyFrom());
            resultToBeTransformed.setCurrencyTo(daoItem.getCurrencyTo());
            resultToBeTransformed.setExchangeRate(daoItem.getExchangeRate());
            resultToBeTransformed.setInsertTime(daoItem.getInsertTime());
            resultToBeTransformed.setUser(daoItem.getUser());
            resultToBeTransformed.setCalculatedAtTime(daoItem.getCalculatedAtTime());
            conversionList.add(resultToBeTransformed);
        }
        return result;
    }
}
