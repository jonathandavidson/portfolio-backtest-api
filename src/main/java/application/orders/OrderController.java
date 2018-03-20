package application.orders;

import application.portfolios.Portfolio;
import application.portfolios.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;

    private final PortfolioRepository portfolioRepository;

    @Autowired
    OrderController(OrderRepository orderRepository, PortfolioRepository portfolioRepository) {
        this.orderRepository = orderRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/portfolios/{portfolioId}/orders")
    public List<Order> index(@PathVariable long portfolioId) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        return this.orderRepository.findByPortfolio(portfolio);
    }

}
