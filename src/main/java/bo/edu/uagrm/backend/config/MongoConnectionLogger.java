package bo.edu.uagrm.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

@Configuration
public class MongoConnectionLogger {

    private static final Logger logger = LoggerFactory.getLogger(MongoConnectionLogger.class);

    @Bean
    CommandLineRunner logMongoDatabase(MongoTemplate mongoTemplate, ConfigurableEnvironment environment) {
        return args -> {
            logger.info("MongoDB database activa: {}", mongoTemplate.getDb().getName());
            logger.info("MongoDB URI efectiva: {}", environment.getProperty("spring.mongodb.uri"));
            logger.info("MongoDB database configurada: {}", environment.getProperty("spring.mongodb.database"));
            for (PropertySource<?> source : environment.getPropertySources()) {
                Object value = source.getProperty("spring.mongodb.uri");
                if (value != null) {
                    logger.info("spring.mongodb.uri encontrado en '{}': {}", source.getName(), value);
                }
            }
        };
    }
}
