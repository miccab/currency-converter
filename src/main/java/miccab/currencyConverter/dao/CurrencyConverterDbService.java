package miccab.currencyConverter.dao;

import org.springframework.stereotype.Component;
import rx.Observable;

/**
 * Created by michal on 23.09.15.
 */
@Component
public class CurrencyConverterDbService {

    public Observable<Long> save(CurrencyConversion currencyConversion) {
        return Observable.just(Long.valueOf(1));
    }
}
