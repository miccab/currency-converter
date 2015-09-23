package miccab.currencyConverter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

/**
 * Created by michal on 23.09.15.
 */
public abstract class BaseController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    protected String getCurrentUser() {
        // TODO
        return "anonymous";
    }

    protected  <T> void handleError(Throwable error, DeferredResult<T> deferredResult) {
        LOG.error("Unexpected error", error);
        deferredResult.setErrorResult(error);
    }

    protected  <T> void handleResult(DeferredResult<T> deferredResult, Observable<T> observableResult) {
        observableResult.subscribe(
                deferredResult::setResult,
                errorFromService -> handleError(errorFromService, deferredResult)
        );
    }


}
