package io.hexlet.callbooking.shared.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

    @GetMapping({"/", "/admin"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
