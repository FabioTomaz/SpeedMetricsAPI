package com.factorypal.demo.controller;


import com.factorypal.demo.model.SpeedEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SpeedController.class)
public class SpeedControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SpeedController arrivalController;

    @BeforeEach
    public void init() throws Exception {
        SpeedEntry speedEntry = new SpeedEntry(10L, 100.0, System.currentTimeMillis());

        mvc.perform(
                post("/linespeed").content(asJsonString(speedEntry)).contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void getMetrics() throws Exception {
        mvc.perform(get("/metrics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$[0].line_id", is(10)));
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}