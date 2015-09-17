package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.dto.Error;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateProvider;
import miccab.currencyConverter.exchangeRate.api.LatestExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

/**
 * Created by michal on 17.09.15.
 */
@RestController
public class CurrencyConverterController {

    private LatestExchangeRateProvider latestExchangeRateProvider;

    @Autowired
    public void setLatestExchangeRateProvider(LatestExchangeRateProvider latestExchangeRateProvider) {
        this.latestExchangeRateProvider = latestExchangeRateProvider;
    }

    @RequestMapping(value = "/currencyConverter", method = RequestMethod.POST)
    public DeferredResult<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest request) {
        final DeferredResult<CurrencyConverterResponse> currencyConversionResult = new DeferredResult<CurrencyConverterResponse>();
        Observable<LatestExchangeRateResponse> latestExchangeRate = latestExchangeRateProvider.getLatestExchangeRate(request.toLatestExchangeRequest());
        latestExchangeRate.subscribe(
                latestExchangeRateResponse -> currencyConversionResult.setResult(CurrencyConverterResponse.fromLatestExchangeResponse(latestExchangeRateResponse)),
                errorFromLatestExchangeProvider -> currencyConversionResult.setErrorResult(new Error(errorFromLatestExchangeProvider.getMessage()))
        );
        return currencyConversionResult;
    }


}
