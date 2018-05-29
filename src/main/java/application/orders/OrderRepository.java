package application.orders;

import application.portfolios.Portfolio;
import application.securities.Security;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPortfolio(Portfolio portfolio);

    List<Order> findBySecurityAndDateLessThan(Security security, Date date);

    List<Order> findByPortfolioAndSecurityAndDateLessThan(Portfolio portfolio, Security security, Date date);
}
