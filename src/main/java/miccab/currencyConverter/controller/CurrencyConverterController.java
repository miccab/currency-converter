package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.service.CurrencyConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;


/**
 * Created by michal on 17.09.15.
 */
@RestController(value = "/currencyConverter")
public class CurrencyConverterController extends BaseController {
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

    @RequestMapping(method = RequestMethod.GET)
    public DeferredResult<CurrencyConversionsResponse> getRecentConversionsForUser() {
        final DeferredResult<CurrencyConversionsResponse> currencyConversions = deferredResultFactory.createDeferredResult();
        final Observable<CurrencyConversionsResponse> observableConversions = currencyConverterService.getRecentConversionsForUser(getCurrentUser());
        handleResult(currencyConversions, observableConversions);
        return currencyConversions;
    }

    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest request) {
        final DeferredResult<CurrencyConverterResponse> currencyConversionResult = deferredResultFactory.createDeferredResult();
        Observable<CurrencyConverterResponse> observableConversionResponse = currencyConverterService.convertCurrency(request, getCurrentUser());
        handleResult(currencyConversionResult, observableConversionResponse);
        return currencyConversionResult;
    }
}
