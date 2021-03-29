package com.factorypal.demo.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.util.List;

@Configuration
@PropertySource("classpath:configprops.properties")
@ConfigurationProperties(prefix = "appconf")
@Validated
public class ConfigProperties {

    private List<Long> ids;

    @Min(0)
    private int periodMinutes;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public int getPeriodMinutes() {
        return periodMinutes;
    }

    public void setPeriodMinutes(int periodMinutes) {
        this.periodMinutes = periodMinutes;
    }

}