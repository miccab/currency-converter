package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.util.Optional;

/**
 * Created by michal on 17.09.15.
 */
@RestController
public class CurrencyConverterController {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyConverterController.class);
    private LatestExchangeRateProvider latestExchangeRateProvider;
    private DeferredResultFactory deferredResultFactory;

    @Autowired
    public void setLatestExchangeRateProvider(LatestExchangeRateProvider latestExchangeRateProvider) {
        this.latestExchangeRateProvider = latestExchangeRateProvider;
    }

    @Autowired
    public void setDeferredResultFactory(DeferredResultFactory deferredResultFactory) {
        this.deferredResultFactory = deferredResultFactory;
    }

    @RequestMapping(value = "/currencyConverter", method = RequestMethod.POST)
    public DeferredResult<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest request) {
        final DeferredResult<CurrencyConverterResponse> currencyConversionResult = deferredResultFactory.createDeferredResult();
        // TODO: support historical rates
        Observable<Optional<LatestExchangeRateResponse>> latestExchangeRate = latestExchangeRateProvider.getLatestExchangeRate(request.toLatestExchangeRequest());
        latestExchangeRate.subscribe(
                latestExchangeRateResponse -> currencyConversionResult.setResult(CurrencyConverterResponse.fromLatestExchangeResponse(request, latestExchangeRateResponse)),
                (errorFromLatestExchangeProvider) -> handleError(errorFromLatestExchangeProvider, currencyConversionResult)
        );
        // TODO: persist responses
        return currencyConversionResult;
    }

    private <T> void handleError(Throwable error, DeferredResult<T> deferredResult) {
        LOG.error("Unexpected error", error);
        deferredResult.setErrorResult(error);
    }

}
