package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import miccab.currencyConverter.service.CurrencyConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private DeferredResultFactory deferredResultFactory;
    private CurrencyConverterService currencyConverterService;

    @Autowired
    public void setCurrencyConverterService(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @Autowired
    public void setDeferredResultFactory(DeferredResultFactory deferredResultFactory) {
        this.deferredResultFactory = deferredResultFactory;
    }

    @RequestMapping(value = "/currencyConverter", method = RequestMethod.POST)
    public DeferredResult<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest request) {
        final DeferredResult<CurrencyConverterResponse> currencyConversionResult = deferredResultFactory.createDeferredResult();
        Observable<CurrencyConverterResponse> observableConversionResponse = currencyConverterService.convertCurrency(request, getCurrentUser());
        observableConversionResponse.subscribe(
                currencyConversionResult::setResult,
                (errorFromLatestExchangeProvider) -> handleError(errorFromLatestExchangeProvider, currencyConversionResult)
        );
        return currencyConversionResult;
    }

    private String getCurrentUser() {
        // TODO
        return "anonymous";
    }

    private <T> void handleError(Throwable error, DeferredResult<T> deferredResult) {
        LOG.error("Unexpected error", error);
        deferredResult.setErrorResult(error);
    }

}
