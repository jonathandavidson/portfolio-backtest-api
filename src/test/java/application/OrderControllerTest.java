package application;

import application.orders.Order;
import application.orders.OrderRepository;
import application.portfolios.Portfolio;
import application.portfolios.PortfolioRepository;
import application.securities.Security;
import application.securities.SecurityRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import portfolio.OrderType;

import java.nio.charset.Charset;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private SecurityRepository securityRepository;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private void setupOrder(Portfolio portfolio, String securitySymbol, int quantity) {
        securityRepository.save(new Security(securitySymbol));
        portfolioRepository.save(portfolio);
        orderRepository.save(new Order(portfolio,
                OrderType.BUY, securityRepository.findBySymbol(securitySymbol), quantity, new Date(1514764800000L)));
    }

    @Before
    public void setup() {
        orderRepository.deleteAllInBatch();
        portfolioRepository.deleteAllInBatch();
        securityRepository.deleteAllInBatch();

        Portfolio testPortfolio1 = new Portfolio("Test Portfolio1", "");
        Portfolio testPortfolio2 = new Portfolio("Test Portfolio2", "");

        setupOrder(testPortfolio1, "FOO", 1);
        setupOrder(testPortfolio2, "BAR", 2);
        setupOrder(testPortfolio1, "BAZ", 3);
    }

    @Test
    public void getOrders() throws Exception {
        long portfolioId = portfolioRepository.findAll().get(0).getId();

        mvc.perform(get("/portfolios/" + portfolioId + "/orders").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].quantity", is(1)))
                .andExpect(jsonPath("$[0].date", is(1514764800000L)))
                .andExpect((jsonPath("$[0].type", is("BUY"))))
                .andExpect((jsonPath("$[0].security.symbol", is("FOO"))));
    }

    @Test
    public void deleteOrder() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();
        long portfolioId = order.getPortfolioId();

        mvc.perform(delete("/portfolios/" + portfolioId + "/orders/" + orderId)
                .accept(contentType))
                .andExpect(status().isOk());

        mvc.perform(get("/portfolios/" + portfolioId + "/orders/").accept(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].security.symbol", is("BAZ")));
    }

    @Test
    public void deleteOrderThrows404ErrorWhenOrderIdNotFound() throws Exception {
        long portfolioId = portfolioRepository.findAll().get(0).getId();
        mvc.perform(delete("/portfolios/" + portfolioId + "/1000").accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrderThrows404ErrorWhenPortfolioIdNotFound() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();

        mvc.perform(delete("/portfolios/1000/orders/" + orderId).accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrderThrows404ErrorWhenOrderIdNotFoundInPortfolio() throws Exception {
        long orderId = orderRepository.findAll().get(0).getId();

        Order order = orderRepository.findAll().get(1);
        long portfolioId = order.getPortfolioId();

        mvc.perform(delete("/portfolios/" + portfolioId + "/orders/" + orderId).accept(contentType))
                .andExpect(status().isNotFound());
    }
}
