package application;

import application.orders.Order;
import application.orders.OrderRepository;
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
    private SecurityRepository securityRepository;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Before
    public void setup() {
        securityRepository.save(new Security("TEST"));
        orderRepository.save(new Order(OrderType.BUY,
                securityRepository.findBySymbol("TEST"), 5, new Date(1514764800000L)));
    }

    @Test
    public void getOrders() throws Exception {
        mvc.perform(get("/orders").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity", is(5)))
                .andExpect(jsonPath("$[0].date", is(1514764800000L)))
                .andExpect((jsonPath("$[0].type", is("BUY"))))
                .andExpect((jsonPath("$[0].security.symbol", is("TEST"))));
    }

}
