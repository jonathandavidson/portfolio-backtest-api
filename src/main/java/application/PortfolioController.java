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

    @PostMapping
    public Portfolio add(@RequestBody Portfolio input) {
        return portfolioRepository.save(
                new Portfolio(input.getName(), input.getDescription()));
    }

    @GetMapping
    public List<Portfolio> index() {
        return portfolioRepository.findAll();
    }

    @GetMapping("/{portfolioId}")
    public Portfolio getById(@PathVariable long portfolioId) {
        return portfolioRepository.findOne(portfolioId);
    }

    @DeleteMapping("/{portfolioId}")
    public void delete(@PathVariable long portfolioId) throws ResourceNotFoundException {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        if (portfolio == null) {
            throw new ResourceNotFoundException(String.format(
                "The portfolio with id %d does not exist", portfolioId));
        }
        portfolioRepository.delete(portfolio);
    }
}