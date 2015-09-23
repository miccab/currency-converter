package miccab.currencyConverter;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.glassfish.jersey.client.rx.RxClient;
import org.glassfish.jersey.client.rx.rxjava.RxObservable;
import org.glassfish.jersey.client.rx.rxjava.RxObservableInvoker;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Created by michal on 20.09.15.
 */
@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public RxClient<RxObservableInvoker> createRxClient() {
        // TODO: configure socket read/connect timeout
        return RxObservable.newClient();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        return new ServletRegistrationBean(new HystrixMetricsStreamServlet(),"/hystrix.stream");
    }
}
