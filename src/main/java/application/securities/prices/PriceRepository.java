package application.securities.prices;

import application.securities.Security;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    public List<Price> findBySecurity(Security security);

    public List<Price> findBySecurityAndDateBetween(
            Security security, Date startDate, Date endDate);

}
