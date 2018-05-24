package application;

import application.orders.Order;
import application.orders.OrderRepository;
import application.portfolios.Portfolio;
import application.portfolios.PortfolioRepository;
import application.securities.Security;
import application.securities.SecurityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    ObjectMapper objectMapper;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private Security createSecurity(String symbol) {
        return securityRepository.save(new Security(symbol));
    }

    private void setupOrder(Portfolio portfolio, String securitySymbol, int quantity, Date date) {
        portfolioRepository.save(portfolio);
        orderRepository.save(new Order(portfolio,
                OrderType.BUY, securityRepository.findBySymbol(securitySymbol), quantity, date));
    }

    private String getUrl(long portfolioId) {
        return "/portfolios/" + portfolioId + "/orders";
    }

    private String getUrl(long portfolioId, long orderId) {
        return getUrl(portfolioId) + "/" + orderId;
    }

    @Before
    public void setup() {
        Portfolio testPortfolio1 = new Portfolio("Test Portfolio1", "");
        Portfolio testPortfolio2 = new Portfolio("Test Portfolio2", "");

        Date date = new Date(1514764800000L);

        createSecurity("FOO");
        createSecurity("BAR");
        createSecurity("BAZ");

        setupOrder(testPortfolio1, "FOO", 1, date);
        setupOrder(testPortfolio2, "BAR", 2, date);
        setupOrder(testPortfolio1, "BAZ", 3, date);
    }

    @After
    public void tearDown() {
        orderRepository.deleteAllInBatch();
        portfolioRepository.deleteAllInBatch();
        securityRepository.deleteAllInBatch();
    }

    @Test
    public void getOrders() throws Exception {
        long portfolioId = portfolioRepository.findAll().get(0).getId();

        mvc.perform(get(getUrl(portfolioId)).accept(contentType))
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

        mvc.perform(delete(getUrl(portfolioId, orderId))
                .accept(contentType))
                .andExpect(status().isOk());

        mvc.perform(get(getUrl(portfolioId)).accept(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].security.symbol", is("BAZ")));
    }

    @Test
    public void deleteOrderThrows404ErrorWhenOrderIdNotFound() throws Exception {
        long portfolioId = portfolioRepository.findAll().get(0).getId();
        mvc.perform(delete(getUrl(portfolioId, 1000)).accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrderThrows404ErrorWhenPortfolioIdNotFound() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();

        mvc.perform(delete(getUrl(1000, orderId)).accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrderThrows404ErrorWhenOrderIdNotFoundInPortfolio() throws Exception {
        long orderId = orderRepository.findAll().get(0).getId();

        Order order = orderRepository.findAll().get(1);
        long portfolioId = order.getPortfolioId();

        mvc.perform(delete(getUrl(portfolioId, orderId)).accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addBuyOrder() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);

        Order order = new Order(portfolio,
                OrderType.BUY, securityRepository.findBySymbol("FOO"), 10, new Date(1514764800001L));

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.date", is(1514764800001L)))
                .andExpect((jsonPath("$.type", is("BUY"))))
                .andExpect((jsonPath("$.security.symbol", is("FOO"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolio.getId()))));
    }


    @Test
    public void addSellOrder() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);

        Order order = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("FOO"), 1, new Date(1514764800005L));

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(1)))
                .andExpect(jsonPath("$.date", is(1514764800005L)))
                .andExpect((jsonPath("$.type", is("SELL"))))
                .andExpect((jsonPath("$.security.symbol", is("FOO"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolio.getId()))));
    }

    @Test
    public void addOrderThrows400ErrorWhenTypeIsOmitted() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);
        Order order = new Order(portfolio,
                null, securityRepository.findBySymbol("FOO"), 10, new Date(1514764800001L));

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addOrderThrows400ErrorWhenQuantityIsOmitted() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);
        Order order = new Order(portfolio,
                OrderType.BUY, securityRepository.findBySymbol("FOO"), null, new Date(1514764800001L));

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addOrderThrows400ErrorWhenDateIsOmitted() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);
        Order order = new Order(portfolio,
                OrderType.BUY, securityRepository.findBySymbol("FOO"), 10, null);

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addOrderThrows400ErrorWhenSecurityIdIsOmitted() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);
        Order order = new Order(portfolio,
                OrderType.BUY, null, 10, new Date(1514764800001L));

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addOrderThrows400ErrorWhenSellOrderNotPrecededByAdequateBuyOrders() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);
        setupOrder(portfolio, "FOO", 1, new Date(1514764800001L));
        setupOrder(portfolio, "FOO", 1, new Date(1514764800003L));

        Order order = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("FOO"), 3, new Date(1514764800002L));

        mvc.perform(post(getUrl(portfolio.getId())).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateOrder() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();
        long portfolioId = order.getPortfolioId();

        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        Order newOrder = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("BAR"), 10, new Date(1514764800001L));

        mvc.perform(put(getUrl(portfolioId, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.date", is(1514764800001L)))
                .andExpect((jsonPath("$.type", is("SELL"))))
                .andExpect((jsonPath("$.security.symbol", is("BAR"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolioId))));
    }

    @Test
    public void updateOrderIgnoresNullValuesInRequestBody() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();
        long portfolioId = order.getPortfolioId();

        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        Order newOrder1 = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("BAR"), null, new Date(1514764800001L));

        mvc.perform(put(getUrl(portfolioId, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(1)))
                .andExpect(jsonPath("$.date", is(1514764800001L)))
                .andExpect((jsonPath("$.type", is("SELL"))))
                .andExpect((jsonPath("$.security.symbol", is("BAR"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolioId))));

        Order newOrder2 = new Order(portfolio,
                null, securityRepository.findBySymbol("BAR"), 10, new Date(1514764800001L));

        mvc.perform(put(getUrl(portfolioId, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder2)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.date", is(1514764800001L)))
                .andExpect((jsonPath("$.type", is("SELL"))))
                .andExpect((jsonPath("$.security.symbol", is("BAR"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolioId))));

        Order newOrder3 = new Order(portfolio,
                OrderType.SELL, null, 10, new Date(1514764800001L));

        mvc.perform(put(getUrl(portfolioId, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder3)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.date", is(1514764800001L)))
                .andExpect((jsonPath("$.type", is("SELL"))))
                .andExpect((jsonPath("$.security.symbol", is("BAR"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolioId))));

        Order newOrder4 = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("BAR"), 10, null);

        mvc.perform(put(getUrl(portfolioId, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder4)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.date", is(1514764800001L)))
                .andExpect((jsonPath("$.type", is("SELL"))))
                .andExpect((jsonPath("$.security.symbol", is("BAR"))))
                .andExpect((jsonPath("$.portfolioId", is((int) portfolioId))));
    }

    @Test
    public void updateOrderThrows404ErrorWhenOrderIdNotFound() throws Exception {
        Portfolio portfolio = portfolioRepository.findAll().get(0);

        Order newOrder = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("BAR"), 10, new Date(1514764800001L));

        mvc.perform(put(getUrl(portfolio.getId(), 1000))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateOrderThrows404ErrorWhenPortfolioIdNotFound() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();

        Portfolio portfolio = portfolioRepository.findAll().get(0);

        Order newOrder = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("BAR"), 10, new Date(1514764800001L));

        mvc.perform(put(getUrl(1000, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateOrderThrows404ErrorWhenOrderIdNotFoundInPortfolio() throws Exception {
        Order order = orderRepository.findAll().get(0);
        long orderId = order.getId();
        long portfolioId = portfolioRepository.findAll().get(1).getId();

        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        Order newOrder = new Order(portfolio,
                OrderType.SELL, securityRepository.findBySymbol("BAR"), 10, new Date(1514764800001L));

        mvc.perform(put(getUrl(portfolioId, orderId))
                .accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isNotFound());
    }

}
