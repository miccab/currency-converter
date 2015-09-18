package miccab.currencyConverter.controller;

import miccab.currencyConverter.dto.*;
import miccab.currencyConverter.dto.Error;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by michal on 18.09.15.
 */
@Component
public class DeferredResultFactory {

    private static final Error TIMEOUT_ERROR = new Error("Operation timeout");
    private long operationTimeout = 2000;

    public void setOperationTimeout(long operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    public <T> DeferredResult<T> createDeferredResult() {
        return new DeferredResult<T>(operationTimeout, TIMEOUT_ERROR);
    }

}
