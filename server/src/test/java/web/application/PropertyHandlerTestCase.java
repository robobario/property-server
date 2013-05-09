package web.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import service.environment.EnvironmentService;
import service.environment.model.Application;
import service.environment.model.Environment;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Context.class)
public class PropertyHandlerTestCase {

    private static final String APPLICATION_NAME = "app1";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private EnvironmentService service;

    private MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    @DirtiesContext
    public void testGetProp() throws Exception {
        givenApplicationExistsWithProp("jdbc.password", "monkeybadness");
        this.mockMvc.perform(get(Routes.to().property().getProp(APPLICATION_NAME, "jdbc.password"))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string("monkeybadness"));
    }


    private Application givenApplicationExistsWithProp(String propKey, String propVal) {
        Environment env = service.getCurrentEnvironment();
        Application application = env.createApplication(APPLICATION_NAME);
        application.put(propKey, propVal);
        service.update(env);
        return application;
    }
}
