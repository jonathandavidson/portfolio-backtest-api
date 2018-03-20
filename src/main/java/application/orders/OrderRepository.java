package application.orders;

import application.portfolios.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPortfolio(Portfolio portfolio);
}
