package miccab.currencyConverter.dto;

/**
 * Created by michal on 17.09.15.
 */
public class Error {
    private final String errorDetails;

    public Error(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
