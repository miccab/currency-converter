package miccab.currencyConverter.dao.impl;

import miccab.currencyConverter.dao.CurrencyConversion;
import rx.Observer;

import java.util.List;

/**
 * Created by michal on 23.09.15.
 */
public class RecentCurrencyConversionsFindCommand extends BaseCommand<List<CurrencyConversion>> {
    private final CurrencyConversionRepository currencyConversionRepository;
    private final String currentUser;

    public RecentCurrencyConversionsFindCommand(Observer<List<CurrencyConversion>> observer, CurrencyConversionRepository currencyConversionRepository, String currentUser) {
        super(observer);
        this.currencyConversionRepository = currencyConversionRepository;
        this.currentUser = currentUser;
    }

    @Override
    protected void doRun() {
        observer.onNext(currencyConversionRepository.findFirst10ByUserOrderByInsertTimeDesc(currentUser));
    }
}
