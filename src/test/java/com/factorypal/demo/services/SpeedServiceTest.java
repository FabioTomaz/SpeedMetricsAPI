package com.factorypal.demo.services;


import com.factorypal.demo.controller.SpeedController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(SpeedController.class)
public class SpeedServiceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SpeedController arrivalController;

    @Test
    public void getArrivals() throws Exception {
//        mvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].line_id", is(10)));
    }
}