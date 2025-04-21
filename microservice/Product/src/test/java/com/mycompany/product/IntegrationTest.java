package com.mycompany.product;

import com.mycompany.product.config.AsyncSyncConfiguration;
import com.mycompany.product.config.EmbeddedElasticsearch;
import com.mycompany.product.config.EmbeddedRedis;
import com.mycompany.product.config.EmbeddedSQL;
import com.mycompany.product.config.JacksonConfiguration;
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
@SpringBootTest(classes = { ProductApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
