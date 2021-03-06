package portfolio;/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PortfolioTest {
    @Test
    public void getSecuritiesReturnsEmptyListAfterInitialization() {
        Portfolio portfolio = new Portfolio();
        assertEquals(0, portfolio.getSecurities().size());
    }

    @Test
    public void getSecuritiesReturnsAddedSecurities() {
        Security security1 = new Security();
        Security security2 = new Security();

        Portfolio portfolio = new Portfolio();
        portfolio.addSecurity(security1);
        portfolio.addSecurity(security2);

        List<Security> securities = portfolio.getSecurities();
        assertEquals(security1, securities.get(0));
        assertEquals(security2, securities.get(1));
    }
}
