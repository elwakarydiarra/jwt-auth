package rentals.jwt_auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public String securedEndpoint() {
        return "✅ Accès autorisé avec un token valide.";
    }
}
