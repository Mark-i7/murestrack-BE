package cityissue.tracker.murestrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cityissue.tracker.murestrack.persistence.model.Report;
import cityissue.tracker.murestrack.persistence.model.User;
import cityissue.tracker.murestrack.persistence.model.validator.EntityValidator;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.SecureRandom;

@SpringBootApplication
public class MurestrackApplication {
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	@Bean
	public EntityValidator<User> userValidator() {return new EntityValidator<>();}

	@Bean
	public EntityValidator<Report> reportValidator() {return new EntityValidator<>();}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET","PUT","POST","PATCH","DELETE", "OPTIONS").exposedHeaders("X-Total-Results");
			}
		};
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {return new BCryptPasswordEncoder(10, new SecureRandom());}
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(MurestrackApplication.class, args);


	}

}
