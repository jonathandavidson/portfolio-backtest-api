package application.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/securities")
class SecurityController {

    private final SecurityRepository securityRepository;

    @Autowired
    SecurityController(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    @PostMapping
    public Security add(@RequestBody Security input) {
        return securityRepository.save(
                new Security(input.getSymbol()));
    }

    @GetMapping
    public List<Security> index() {
        return securityRepository.findAll();
    }

}
