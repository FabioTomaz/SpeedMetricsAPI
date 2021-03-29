package com.factorypal.demo.conf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigPropertiesTest.TestConfiguration.class})
public class ConfigPropertiesTest {

    @Autowired
    private ConfigProperties properties;

    @Test
    public void testConfSettings() {
        assertEquals(60, properties.getPeriodMinutes());
        assertTrue(properties.getIds().contains(10L));
        assertTrue(properties.getIds().contains(11L));
        assertEquals(2, properties.getIds().size());
    }

    @EnableConfigurationProperties(ConfigProperties.class)
    public static class TestConfiguration {
        // nothing
    }
}