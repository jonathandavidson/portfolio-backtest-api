package application.securities;

import application.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
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
        Security security = new Security(input.getSymbol());

        try {
            return securityRepository.save(security);
        } catch (ConstraintViolationException e) {
            throw new BadRequestException("The security could not be added as submitted");
        }
    }

    @GetMapping
    public List<Security> index() {
        return securityRepository.findAll();
    }

}
