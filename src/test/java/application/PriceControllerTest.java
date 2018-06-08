package application;

import application.securities.Security;
import application.securities.SecurityRepository;
import application.securities.prices.Price;
import application.securities.prices.PriceRepository;
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

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PriceControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private SecurityRepository securityRepository;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Before
    public void setup() {
        Security sec1 = createSecurity("FOO");
        Security sec2 = createSecurity("BAR");
        createPrice(1514764800000L, sec1, 1.00);
        createPrice(1514764800001L, sec2, 1.50);
    }

    @After
    public void tearDown() {
        priceRepository.deleteAllInBatch();
        securityRepository.deleteAllInBatch();
    }

    private Price createPrice(long date, Security security, double value) {
        Price price = new Price(new Date(date),
                security, BigDecimal.valueOf(value));

        return priceRepository.save(price);
    }

    private Security createSecurity(String symbol) {
        return securityRepository.save(new Security(symbol));
    }

    @Test
    public void getPricesWithNoParamsReturnsAllPrices() throws Exception {
        long securityId = securityRepository.findAll().get(0).getId();
        mvc.perform(get("/securities/" + securityId + "/prices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date", is(1514764800000L)))
                .andExpect(jsonPath("$[0].value", is(1.00)));
    }

    @Test
    public void getPricesThrows404WhenInvalidSecurityIdPassed() throws Exception {
        mvc.perform(get("/securities/1000/prices"))
                .andExpect(status().isNotFound());
    }
}
