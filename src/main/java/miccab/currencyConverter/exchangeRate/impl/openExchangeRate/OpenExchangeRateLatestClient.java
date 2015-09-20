package miccab.currencyConverter.exchangeRate.impl.openExchangeRate;

import org.glassfish.jersey.client.rx.RxClient;
import org.glassfish.jersey.client.rx.rxjava.RxObservableInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by michal on 20.09.15.
 */
@Component
public class OpenExchangeRateLatestClient {

    private RxClient<RxObservableInvoker> rxClient;
    private String secretKey;
    private String url;

    @Autowired
    public void setRxClient(RxClient<RxObservableInvoker> rxClient) {
        this.rxClient = rxClient;
    }

    @Value("${openExchangeRate.secretKey}")
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Value("${openExchangeRate.latestRates.url}")
    public void setUrl(String url) {
        this.url = url;
    }

    public Observable<OpenExchangeRateResponse> getLatestExchangeRate() {
        final Observable<Response> observableResponse = callExternalSystem();
        return observableResponse.map(this::convertRawResponseToOpenExchangeRateResponse);
    }

    private Observable<Response> callExternalSystem() {
        return rxClient.target(url).queryParam("app_id", secretKey).request(MediaType.APPLICATION_JSON_TYPE).rx().get();
    }

    OpenExchangeRateResponse convertRawResponseToOpenExchangeRateResponse(Response response) {
        int responseStatusCode = response.getStatus();
        if (responseStatusCode == HttpStatus.OK.value()) {
            return response.readEntity(OpenExchangeRateResponse.class);
        } else {
            throw new RuntimeException(String.format("Remote service returned unexpected status code: %d", responseStatusCode));
        }
    }

}
