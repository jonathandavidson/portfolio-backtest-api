package application;

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
import application.portfolios.Portfolio;
import application.portfolios.PortfolioRepository;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioControllerTest {

    private String basePath = "/portfolios";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    ObjectMapper objectMapper;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Before
    public void setup() {
        portfolioRepository.save(new Portfolio("Test Name 1", "Test Description 1"));
        portfolioRepository.save(new Portfolio("Test Name 2", "Test Description 2"));
    }

    @After
    public void tearDown() {
        portfolioRepository.deleteAllInBatch();
    }

    @Test
    public void getPortfolioList() throws Exception {
        mvc.perform(get(basePath).accept(contentType))
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
        mvc.perform(get(basePath + "/" + id).accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is("Test Name 1")))
                .andExpect(jsonPath("$.description", is("Test Description 1")));
    }

    @Test
    public void getPortfolioByIdThrows404ErrorWhenIdNotFound() throws Exception {
        mvc.perform(get(basePath + "/1000").accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePortfolio() throws Exception {
        long id = portfolioRepository.findAll().get(0).getId();
        mvc.perform(delete(basePath + "/" + id).accept(contentType))
                .andExpect(status().isOk());

        mvc.perform(get(basePath).accept(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Name 2")));
    }

    @Test
    public void deletePortfolioThrows404ErrorWhenIdNotFound() throws Exception {
        mvc.perform(delete(basePath + "/1000").accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePortfolio() throws Exception {
        Portfolio portfolio = new Portfolio("Test New Name", "Test new description");
        long id = portfolioRepository.findAll().get(0).getId();
        mvc.perform(put(basePath + "/" + id).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(jsonPath("$.name", is("Test New Name")))
                .andExpect(jsonPath("$.description", is("Test new description")));
    }

    @Test
    public void updatePortfolioThrows404ErrorWhenIdNotFound() throws Exception {
        Portfolio portfolio = new Portfolio("Test New Name", "Test new description");
        mvc.perform(put(basePath + "/1000").accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addPortfolio() throws Exception {
        Portfolio portfolio = new Portfolio("Test Add Name", "Test add description");
        mvc.perform(post(basePath).accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(jsonPath("$.name", is("Test Add Name")))
                .andExpect(jsonPath("$.description", is("Test add description")));
    }
}
