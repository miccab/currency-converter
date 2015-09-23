package miccab.currencyConverter.dao.impl;

import miccab.currencyConverter.dao.CurrencyConversion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by michal on 23.09.15.
 */
public interface CurrencyConversionRepository extends CrudRepository<CurrencyConversion, Long> {
    List<CurrencyConversion> findFirst10ByUserOrderByInsertTimeDesc(String currentUser);
}
