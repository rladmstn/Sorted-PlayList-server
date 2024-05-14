package com.my.sorted_playlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SortedPlaylistApplication {
	public static void main(String[] args) {
		SpringApplication.run(SortedPlaylistApplication.class, args);
	}

}
