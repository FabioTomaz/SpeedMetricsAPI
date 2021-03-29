package com.factorypal.demo.controller;


import com.factorypal.demo.SpeedMertricsServiceApplication;
import com.factorypal.demo.conf.ConfigProperties;
import com.factorypal.demo.model.LineMetrics;
import com.factorypal.demo.model.Metrics;
import com.factorypal.demo.model.SpeedEntry;
import com.factorypal.demo.services.SpeedService;
import com.factorypal.demo.util.Operations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.sound.sampled.Line;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {SpeedMertricsServiceApplication.class, SpeedControllerTest.TestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpeedControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private SpeedService speedService;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testEmptyGetMetrics() throws Exception {
        this.checkLineMetrics(10L, new Metrics(0.0, 0.0, 0.0));
    }

    @Test
    public void testOneEntryGetLineMetrics() throws Exception {
        this.insertSpeedEntry( new SpeedEntry(10L, 100.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()-10000));
        this.checkLineMetrics(10L, new Metrics(100.0, 100.0, 100.0));
    }

    @Test
    public void testGetLineMetrics() throws Exception {
        this.insertSpeedEntry( new SpeedEntry(10L, 100.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()-10000));
        this.insertSpeedEntry( new SpeedEntry(10L, 500.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(10L, 250.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(10L, 575.5, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(11L, 1575.5, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.checkLineMetrics(10L, new Metrics(100.0, 575.5, 356.375));
    }

    @Test
    public void testGetLineMetricsHavingDiffLines() throws Exception {
        this.insertSpeedEntry( new SpeedEntry(10L, 200.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(10L, 300.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(11L, 700.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.checkLineMetrics(10L, new Metrics(200.0, 300.0, 250.0));
        this.checkLineMetrics(11L, new Metrics(700.0, 700.0, 700.0));
    }

    @Test
    public void testEmptyGetLineMetrics() throws Exception {
        this.checkLineMetrics(10L, new Metrics(0.0, 0.0, 0.0));
    }

    @Test
    public void testGetAllMetrics() throws Exception {
        this.insertSpeedEntry( new SpeedEntry(11L, 300.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(10L, 300.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(11L, 600.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));
        this.insertSpeedEntry( new SpeedEntry(10L, 200.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()));

        List<LineMetrics> expected = new LinkedList<>();
        expected.add(new LineMetrics(10L, new Metrics(200.0, 300.0, 250.0)));
        expected.add(new LineMetrics(11L, new Metrics(300.0, 600.0, 450.0)));

        this.checkLineMetrics(expected);
    }

    @Test
    public void testGetUnknownLineMetrics() throws Exception {
        mockMvc.perform(get(String.format("/metrics/%d", 12L)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testInsertSpeedFromUnknownLine() throws Exception {
        SpeedEntry speedEntry = new SpeedEntry(20L, 100.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()-10000);
        mockMvc.perform(post("/linespeed").content(asJsonString(speedEntry))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testInsertSpeedOlderThanPeriod() throws Exception {
        SpeedEntry speedEntry = new SpeedEntry(10L, 100.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()-(5*60*60*1000));
        mockMvc.perform(post("/linespeed").content(asJsonString(speedEntry))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testInsertSpeedFromFuture() throws Exception {
        SpeedEntry speedEntry = new SpeedEntry(10L, 100.0, Operations.getCurrentTimestamp(ZoneId.of("UTC")).getTime()+(5*60*60*1000));
        mockMvc.perform(post("/linespeed").content(asJsonString(speedEntry))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllEmptyMetrics() throws Exception {
        List<LineMetrics> expected = new LinkedList<>();

        this.checkLineMetrics(expected);
    }


    private void insertSpeedEntry(SpeedEntry speedEntry) throws Exception {
        mockMvc.perform(post("/linespeed").content(asJsonString(speedEntry))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string("Speed entry added"));
    }

    private void checkLineMetrics(long id, Metrics expectedMetrics) throws Exception {
        mockMvc.perform(get(String.format("/metrics/%d", id)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(expectedMetrics)));
    }

    private void checkLineMetrics(List<LineMetrics> metrics) throws Exception {
        mockMvc.perform(get(String.format("/metrics")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(metrics)));
    }

    private String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EnableConfigurationProperties(ConfigProperties.class)
    public static class TestConfiguration {
        // nothing
    }

}