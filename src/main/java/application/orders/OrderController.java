package application.orders;

import application.BadRequestException;
import application.ResourceNotFoundException;
import application.portfolios.Portfolio;
import application.portfolios.PortfolioRepository;
import application.securities.Security;
import application.securities.prices.Price;
import application.securities.prices.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import portfolio.OrderType;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping("/portfolios")
public class OrderController {

    private final OrderRepository orderRepository;

    private final PortfolioRepository portfolioRepository;

    private final PriceRepository priceRepository;

    @Autowired
    OrderController(
            OrderRepository orderRepository,
            PortfolioRepository portfolioRepository,
            PriceRepository priceRepository
    ) {
        this.orderRepository = orderRepository;
        this.portfolioRepository = portfolioRepository;
        this.priceRepository = priceRepository;
    }

    @GetMapping("/{portfolioId}/orders")
    public List<Order> index(@PathVariable long portfolioId) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        return this.orderRepository.findByPortfolio(portfolio);
    }

    @PostMapping("/{portfolioId}/orders")
    public Order add(@PathVariable long portfolioId, @RequestBody Order input) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        checkDateRange(input, input.getSecurity());

        if (input.getType() == OrderType.SELL) {
            List<Order> orders = orderRepository
                    .findByPortfolioAndSecurityAndDateLessThan(portfolio, input.getSecurity(), input.getDate());

            long sum = orders.stream().mapToInt(o -> o.getQuantity()).sum();

            if (sum < input.getQuantity()) {
                throw new BadRequestException("Invalid order: Can not sell without adequate holdings");
            }
        }

        Order order = new Order(
                portfolio,
                input.getType(),
                input.getSecurity(),
                input.getQuantity(),
                input.getDate()
        );

        return saveOrder(order, "The order could not be created as submitted");
    }

    @PutMapping("/{portfolioId}/orders/{orderId}")
    public Order update(@PathVariable long portfolioId, @PathVariable long orderId, @RequestBody Order input) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        Order order = findOne(orderId, portfolioId);
        order.update(input);
        checkDateRange(input, input.getSecurity());

        if (order.getType() == OrderType.SELL) {
            List<Order> orders = orderRepository
                    .findByPortfolioAndSecurityAndDateLessThan(portfolio, order.getSecurity(), order.getDate());

            long sum = orders.stream()
                    .filter(record -> record.getId() != orderId)
                    .mapToInt(o -> o.getQuantity()).sum();

            if (sum < order.getQuantity()) {
                throw new BadRequestException("Invalid order: Can not sell without adequate holdings");
            }
        }

        return saveOrder(order, "The order could not be updated as submitted");
    }

    @DeleteMapping("/{portfolioId}/orders/{orderId}")
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

    private void checkDateRange(@RequestBody Order input, Security security) {
        if (security != null) {
            Price earliest = priceRepository.findFirstBySecurityOrderByDateAsc(security);
            if (input.getDate() != null && input.getDate().before(earliest.getDate())) {
                throw new BadRequestException("The date can not be earlier than the first security price");
            }
        }
    }

    private Order saveOrder(Order order, String failMessage) {
        try {
            return orderRepository.save(order);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            throw new BadRequestException(failMessage);
        }
    }

}
