package spa.musa.send.mensage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import spa.musa.send.mensage.config.EvolutionConfig;

@SpringBootApplication
@EnableConfigurationProperties(EvolutionConfig.class)
@EnableAsync
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	//mysql -u root -p < src/main/resources/schema.sql
}
