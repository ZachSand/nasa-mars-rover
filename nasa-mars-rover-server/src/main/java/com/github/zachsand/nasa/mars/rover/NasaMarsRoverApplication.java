package com.github.zachsand.nasa.mars.rover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * {@link NasaMarsRoverApplication} Spring Boot Application for utilizing the NASA Mars Rover API for various functionality.
 *
 * @see <a href="https://github.com/chrisccerami/mars-photo-api"/>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class NasaMarsRoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(NasaMarsRoverApplication.class, args);
	}

}
