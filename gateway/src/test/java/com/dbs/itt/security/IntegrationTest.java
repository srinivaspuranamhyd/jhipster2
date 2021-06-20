package com.dbs.itt.security;

import com.dbs.itt.security.GatewayApp;
import com.dbs.itt.security.ReactiveSqlTestContainerExtension;
import com.dbs.itt.security.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { GatewayApp.class, TestSecurityConfiguration.class })
@ExtendWith(ReactiveSqlTestContainerExtension.class)
public @interface IntegrationTest {
}
