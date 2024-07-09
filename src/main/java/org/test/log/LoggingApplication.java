package org.test.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Atousa Mirhosseini
 * @since 08 Jul, 2024
 */
@SpringBootApplication
public class LoggingApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(LoggingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LoggingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            Exception e1 = new Exception("Third Cause");
            Exception e2 = new Exception("Second Cause", e1);
            Exception e3 = new Exception("First Cause", e2);
            Exception e4 = new Exception("Root Exception", e3);
            logger.error("The exeption: ", e4);
        } catch (Exception e) {
            logger.error("An error occurred", e);
        }
    }
}
