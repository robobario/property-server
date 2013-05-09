package web.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import service.environment.model.Environment;
import web.view.EnvironmentView;
import web.view.ViewCreator;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Context.class)
public class EnvironmentHandlerTest {

    private static final String SUB_ENV_NAME = "subenv";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    public void getRootEnvironment() throws Exception {
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(Environment.createRootEnvironment());
        this.mockMvc.perform(get(Routes.to().environment().root()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void addSubEnv() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.createSubEnvironment(SUB_ENV_NAME);
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(put(Routes.to().environment().addSubEnvironmentTo("root", SUB_ENV_NAME)))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void viewSubEnv() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        Environment subenv = rootEnvironment.createSubEnvironment(SUB_ENV_NAME);
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(subenv);
        this.mockMvc.perform(put(Routes.to().environment().addSubEnvironmentTo("root", SUB_ENV_NAME)));
        this.mockMvc.perform(get(Routes.to().environment().environmentDetails(SUB_ENV_NAME))).andExpect(status().isOk())
                .andExpect(content()
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void addApp() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.createApplication("app");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(put(Routes.to().environment().addApplicationTo("root", "app"))).andExpect(status().isOk())
                .andExpect(content()
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void addProp() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.put("p1", "p2");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(postPropCreateRequest("root", "p1", "p2")).andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    private MockHttpServletRequestBuilder postPropCreateRequest(String env, String key, String value)
            throws JsonProcessingException {
        return post(Routes.to().environment().addPropertyTo(env)).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new AddPropRequest(key, value)));
    }


    @Test
    @DirtiesContext
    public void addPropToSubEnv() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        Environment sub = rootEnvironment.createSubEnvironment("sub");
        sub.put("p1", "p2");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(put(Routes.to().environment().addSubEnvironmentTo("root", "sub")));
        this.mockMvc.perform(postPropCreateRequest("sub", "p1", "p2"));
        this.mockMvc.perform(get(Routes.to().environment().root()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void addComplexProp() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        Environment sub = rootEnvironment.createSubEnvironment("sub");
        String key = "activemqurl";
        String value = "tcp://${activemqport}:${activemqport}";
        sub.put(key, value);
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(put(Routes.to().environment().addSubEnvironmentTo("root", "sub")));
        this.mockMvc.perform(postPropCreateRequest("sub", key, value));
        this.mockMvc.perform(get(Routes.to().environment().root()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void addPropPersists() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.put("p1", "p2");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(postPropCreateRequest("root", "p1", "p2"));
        this.mockMvc.perform(get(Routes.to().environment().root())).andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }


    @Test
    @DirtiesContext
    public void removeProp() throws Exception {
        Environment rootEnvironment = Environment.createRootEnvironment();
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        this.mockMvc.perform(put(Routes.to().environment().addPropertyTo("root"), new AddPropRequest("p1", "p2")));
        this.mockMvc.perform(delete(Routes.to().environment().removePropertyFrom("root", "p1")))
                .andExpect(status().isOk()).andExpect(
                content().contentType(new MediaType(MediaType.APPLICATION_JSON, ImmutableMap.of("charset", "UTF-8"))))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(environmentView)));
    }
}
