package application.portfolios;

import application.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolios")
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

    @PutMapping("/{portfolioId}")
    public Portfolio edit(@PathVariable long portfolioId, @RequestBody Portfolio input) {
        Portfolio portfolio = findOne(portfolioId);
        portfolio.update(input);
        return portfolioRepository.save(portfolio);
    }

    @GetMapping("/{portfolioId}")
    public Portfolio getById(@PathVariable long portfolioId) {
        return findOne(portfolioId);
    }

    @DeleteMapping("/{portfolioId}")
    public void delete(@PathVariable long portfolioId) throws ResourceNotFoundException {
        Portfolio portfolio = findOne(portfolioId);
        portfolioRepository.delete(portfolio);
    }

    private Portfolio findOne(long portfolioId) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        if (portfolio == null) {
            throw new ResourceNotFoundException(String.format(
                    "The portfolio with id %d does not exist", portfolioId));
        }
        return portfolio;
    }
}