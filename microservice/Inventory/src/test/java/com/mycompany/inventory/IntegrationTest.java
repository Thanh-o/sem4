package com.mycompany.inventory;

import com.mycompany.inventory.config.AsyncSyncConfiguration;
import com.mycompany.inventory.config.EmbeddedElasticsearch;
import com.mycompany.inventory.config.EmbeddedRedis;
import com.mycompany.inventory.config.EmbeddedSQL;
import com.mycompany.inventory.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { InventoryApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
