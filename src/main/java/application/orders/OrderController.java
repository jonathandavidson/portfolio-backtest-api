package application.orders;

import application.BadRequestException;
import application.ResourceNotFoundException;
import application.portfolios.Portfolio;
import application.portfolios.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
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

    @PostMapping("/portfolios/{portfolioId}/orders")
    public Order add(@PathVariable long portfolioId, @RequestBody Order input) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        try {
            return orderRepository.save(
                    new Order(portfolio, input.getType(), input.getSecurity(), input.getQuantity(), input.getDate()));
        } catch (ConstraintViolationException e) {
            throw new BadRequestException("The order could not be created as submitted");
        }
    }

    @PutMapping("/portfolios/{portfolioId}/orders/{orderId}")
    public Order add(@PathVariable long portfolioId, @PathVariable long orderId, @RequestBody Order input) {
        Order order = findOne(orderId, portfolioId);
        order.update(input);
        return orderRepository.save(order);
    }

    @DeleteMapping("/portfolios/{portfolioId}/orders/{orderId}")
    public void delete(@PathVariable long portfolioId, @PathVariable long orderId) {
        Order order = findOne(orderId, portfolioId);
        orderRepository.delete(order);
    }

    private Order findOne(long orderId, long portfolioId) {
        Order order = orderRepository.findOne(orderId);
        if (order == null) {
            throw new ResourceNotFoundException(String.format(
                    "The order with id %d does not exist", orderId));
        } else if (order.getPortfolioId() != portfolioId) {
            throw new ResourceNotFoundException((String.format(
                    "The order with id %d does not exist in portfolio with id %d", orderId, portfolioId)));
        }
        return order;
    }

}
