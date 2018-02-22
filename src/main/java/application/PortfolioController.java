package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
class PortfolioController {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    PortfolioController(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Portfolio add(@RequestBody Portfolio input) {
        return this.portfolioRepository.save(
                new Portfolio(input.getName(), input.getDescription()));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Portfolio> index() {
        return this.portfolioRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{portfolioId}")
    public Portfolio getById(@PathVariable long portfolioId) {
        return this.portfolioRepository.findOne(portfolioId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{portfolioId}")
    public void delete(@PathVariable long portfolioId) throws Exception {
        Portfolio portfolio = getById(portfolioId);
        if (portfolio == null) {
            throw new Exception("Portfolio does not exist");
        }
        this.portfolioRepository.delete(portfolio);
    }
}