package web.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import service.environment.EnvironmentService;
import service.environment.model.Application;
import service.environment.model.Environment;
import web.view.ApplicationView;
import web.view.ViewCreator;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Context.class)
public class ApplicationHandlerTest {

    private static final String SUB_ENV_NAME = "subenv";

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
    public void getApplication() throws Exception {
        Application application = givenApplicationExists();
        ApplicationView applicationView = ViewCreator.createApplicationView(application);
        this.mockMvc.perform(
                get(Routes.to().application().applicationDetails(APPLICATION_NAME)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(applicationView)));
    }


    @Test
    @DirtiesContext
    public void putProperty() throws Exception {
        Application application = givenApplicationExists();
        application.put("p1", "p2");
        ApplicationView applicationView = ViewCreator.createApplicationView(application);
        this.mockMvc.perform(postPropCreateRequest(APPLICATION_NAME, "p1", "p2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(applicationView)));
    }


    @Test
    @DirtiesContext
    public void removeProperty() throws Exception {
        Application application = givenApplicationExistsWithProps();
        application.remove("p1");
        ApplicationView applicationView = ViewCreator.createApplicationView(application);
        this.mockMvc.perform(delete(Routes.to().application().removeProperty(APPLICATION_NAME, "p1"))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(applicationView)));
    }


    private Application givenApplicationExistsWithProps() {
        Environment env = service.getCurrentEnvironment();
        Application application = env.createApplication(APPLICATION_NAME);
        application.put("p1", "p2");
        service.update(env);
        return application;
    }


    private MockHttpServletRequestBuilder postPropCreateRequest(String app, String key, String value)
            throws JsonProcessingException {
        return post(Routes.to().application().addProperty(app)).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new AddPropRequest(key, value)));
    }


    private Application givenApplicationExists() {
        Environment env = service.getCurrentEnvironment();
        Application application = env.createApplication(APPLICATION_NAME);
        service.update(env);
        return application;
    }
}
