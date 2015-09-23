package miccab.currencyConverter.dao;

import miccab.currencyConverter.dao.impl.CurrencyConversionRepository;
import miccab.currencyConverter.dao.impl.CurrencyConversionSaveCommand;
import miccab.currencyConverter.dao.impl.RecentCurrencyConversionsFindCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.subjects.AsyncSubject;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by michal on 23.09.15.
 */
@Component
public class CurrencyConverterDbService {

    private Executor executorForWriteOperation;
    private Executor executorForReadOperation;
    private CurrencyConversionRepository currencyConversionRepository;

    @Autowired (required = false)
    public void setExecutorForReadOperation(Executor executorForReadOperation) {
        this.executorForReadOperation = executorForReadOperation;
    }

    @Autowired (required = false)
    public void setExecutorForWriteOperation(Executor executorForWriteOperation) {
        this.executorForWriteOperation = executorForWriteOperation;
    }

    @Autowired
    public void setCurrencyConversionRepository(CurrencyConversionRepository currencyConversionRepository) {
        this.currencyConversionRepository = currencyConversionRepository;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        if (executorForWriteOperation == null) {
            executorForWriteOperation = createExecutorForWriteOperation();
        }
        if (executorForReadOperation == null) {
            executorForReadOperation = createExecutorForReadOperation();
        }
    }

    private Executor createExecutorForReadOperation() {
        // TODO: move it to factory. use bounded queue. Size should be same that we have DB pool size.
        return Executors.newFixedThreadPool(10);
    }

    private Executor createExecutorForWriteOperation() {
        // TODO: move it to factory to define threadfactory and also use bounded queue
        return Executors.newSingleThreadExecutor();
    }

    public Observable<Long> save(CurrencyConversion currencyConversion) {
        final AsyncSubject<Long> subject = AsyncSubject.create();
        // using single thread to avoid any DB contention during write operation
        executorForWriteOperation.execute(new CurrencyConversionSaveCommand(subject, currencyConversion, currencyConversionRepository));
        return subject;
    }

    public Observable<List<CurrencyConversion>> getRecentConversionsForUser(String currentUser) {
        final AsyncSubject<List<CurrencyConversion>> subject = AsyncSubject.create();
        executorForReadOperation.execute(new RecentCurrencyConversionsFindCommand(subject, currencyConversionRepository, currentUser));
        return subject;
    }
}
