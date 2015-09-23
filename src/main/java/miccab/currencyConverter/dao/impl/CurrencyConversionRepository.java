package miccab.currencyConverter.dao.impl;

import miccab.currencyConverter.dao.CurrencyConversion;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by michal on 23.09.15.
 */
public interface CurrencyConversionRepository extends CrudRepository<CurrencyConversion, Long> {
}
