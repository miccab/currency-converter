package miccab.currencyConverter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by michal on 18.09.15.
 */
@Component
public class DeferredResultFactory {

    private static final Object TIMEOUT_ERROR = new RuntimeException("Operation timeout");
    private long operationTimeout;

    @Value("${webEndpoint.operationTimeout.millis}")
    public void setOperationTimeout(long operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    public <T> DeferredResult<T> createDeferredResult() {
        return new DeferredResult<T>(operationTimeout, TIMEOUT_ERROR);
    }

}
