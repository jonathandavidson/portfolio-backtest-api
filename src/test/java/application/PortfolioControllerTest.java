package application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));

    @Before
    public void setup() {
        portfolioRepository.deleteAllInBatch();
        portfolioRepository.save(new Portfolio("Test Name 1", "Test Description 1"));
        portfolioRepository.save(new Portfolio("Test Name 2", "Test Description 2"));
    }

    @Test
    public void getPortfolioList() throws Exception {
        mvc.perform(get("/portfolio").accept(contentType))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("Test Name 1")))
            .andExpect(jsonPath("$[0].description", is("Test Description 1")))
            .andExpect(jsonPath("$[1].name", is("Test Name 2")))
            .andExpect(jsonPath("$[1].description", is("Test Description 2")));
    }

    @Test
    public void getPortfolioById() throws Exception {
        long id = portfolioRepository.findAll().get(0).getId();
        mvc.perform(get("/portfolio/" + id).accept(contentType))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.name", is("Test Name 1")))
            .andExpect(jsonPath("$.description", is("Test Description 1")));
    }

    @Test
    public void deletePortfolio() throws Exception {
        long id = portfolioRepository.findAll().get(0).getId();
        mvc.perform(delete("/portfolio/" + id).accept(contentType))
            .andExpect(status().isOk());

        mvc.perform(get("/portfolio").accept(contentType))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("Test Name 2")));
    }

    @Test
    public void deletePortfolioThrows404ErrorWhenIdNotFound() throws Exception {
        mvc.perform(delete("/portfolio/3").accept(contentType))
            .andExpect(status().isNotFound());
    }
}
