package com.awstraining.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
@EnableScheduling
public class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    public static void main(final String[] args) {
        try {
            new SpringApplicationBuilder(Application.class).bannerMode(OFF).run(args).start();
        } catch (final Throwable e) {
            LOGGER.error("Error while starting the app.", e);
        }
    }

}
