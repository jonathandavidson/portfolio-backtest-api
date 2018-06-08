package application.securities.prices;

import application.ResourceNotFoundException;
import application.securities.Security;
import application.securities.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PriceController {

    private final PriceRepository priceRepository;

    private final SecurityRepository securityRepository;

    @Autowired
    public PriceController(PriceRepository priceRepository, SecurityRepository securityRepository) {
        this.priceRepository = priceRepository;
        this.securityRepository = securityRepository;
    }

    @GetMapping("/securities/{securityId}/prices")
    public List<Price> getPrices(@PathVariable long securityId) {
        Security security = securityRepository.findOne(securityId);

        if (security == null) {
            throw new ResourceNotFoundException(
                    String.format("The security with id %d does not exist", securityId));
        }

        return priceRepository.findBySecurity(security);
    }

}
