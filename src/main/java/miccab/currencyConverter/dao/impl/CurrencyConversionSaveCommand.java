package miccab.currencyConverter.dao.impl;

import miccab.currencyConverter.dao.CurrencyConversion;
import rx.Observer;

/**
 * Created by michal on 23.09.15.
 */
public class CurrencyConversionSaveCommand extends BaseCommand<Long> {
    private final CurrencyConversionRepository currencyConversionRepository;
    private final CurrencyConversion currencyConversion;

    public CurrencyConversionSaveCommand(Observer<Long> observer, CurrencyConversion currencyConversion, CurrencyConversionRepository currencyConversionRepository) {
        super(observer);
        this.currencyConversionRepository = currencyConversionRepository;
        this.currencyConversion = currencyConversion;
    }

    @Override
    protected void doRun() {
        final CurrencyConversion saved = currencyConversionRepository.save(currencyConversion);
        observer.onNext(saved.getId());
    }
}
