package com.tui;

import com.tui.config.GithubProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({GithubProperties.class})
public class TuiTechTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TuiTechTaskApplication.class, args);
	}

}
