package miccab.currencyConverter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by michal on 17.09.15.
 */
@RestController
public class MainController {
    @RequestMapping("/")
    public String main() {
        return "Welcome!";
    }
}
