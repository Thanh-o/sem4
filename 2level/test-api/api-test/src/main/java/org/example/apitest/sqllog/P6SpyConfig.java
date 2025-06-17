package org.example.apitest.sqllog;

import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class P6SpyConfig {

    @PostConstruct
    public void initP6Spy() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomP6SpyFormatter.class.getName());
    }
}
