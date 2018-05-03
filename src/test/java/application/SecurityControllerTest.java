package application;

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

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    ObjectMapper objectMapper;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Before
    public void setup() {
        securityRepository.save(new Security("TEST"));
        securityRepository.save(new Security("TEST2"));
    }

    @After
    public void tearDown() {
        securityRepository.deleteAllInBatch();
    }

    @Test
    public void getSecurityList() throws Exception {
        mvc.perform(get("/securities").accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].symbol", is("TEST")))
                .andExpect(jsonPath("$[1].symbol", is("TEST2")));
    }

    @Test
    public void addSecurity() throws Exception {
        Security security = new Security("FOO");
        mvc.perform(post("/securities").accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(security)))
                .andExpect(jsonPath("$.symbol", is("FOO")));
    }

    @Test
    public void addSecurityThrows400ErrorWhenNameIsOmitted() throws Exception {
        Security security = new Security(null);
        mvc.perform(post("/securities").accept(contentType)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(security)))
                .andExpect(status().isBadRequest());
    }

}
