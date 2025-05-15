package app;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InternalSupportApplication {

	public static void main(String[] args) {
		Dotenv.configure().directory("src/main/resources").load();
		SpringApplication.run(InternalSupportApplication.class, args);
	}

}
