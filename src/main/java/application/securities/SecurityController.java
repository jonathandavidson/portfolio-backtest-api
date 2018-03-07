package application.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/securities")
class SecurityController {

    private final SecurityRepository securityRepository;

    @Autowired
    SecurityController(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    @GetMapping
    public List<Security> index() {
        return securityRepository.findAll();
    }

}
