package application.securities.prices;

import application.BadRequestException;
import application.ResourceNotFoundException;
import application.securities.Security;
import application.securities.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
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
    public List<Price> getPrices(
            @PathVariable long securityId,
            @RequestParam(name = "start", required = false) String startDateString,
            @RequestParam(name = "end", required = false) String endDateString
    ) {
        Date startDate = parseDate(startDateString, "Invalid start date provided");
        Date endDate = parseDate(endDateString, "Invalid end date provided");

        Security security = securityRepository.findOne(securityId);

        if (security == null) {
            throw new ResourceNotFoundException(
                    String.format("The security with id %d does not exist", securityId));
        }

        if (startDate == null) {
            return priceRepository.findBySecurity(security);
        } else {
            return priceRepository.findBySecurityAndDateBetween(security, startDate, endDate);
        }
    }

    private Date parseDate(String dateString, String message) {
        Date date = null;
        if (dateString != null) {
            try {
                date = DatatypeConverter.parseDateTime(dateString).getTime();
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(message);
            }
        }
        return date;
    }

}
