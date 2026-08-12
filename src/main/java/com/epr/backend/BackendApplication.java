package com.epr.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		loadDotEnv();
		SpringApplication.run(BackendApplication.class, args);
	}

	private static void loadDotEnv() {
		Path envFile = Path.of(System.getProperty("user.dir"), ".env");
		if (!Files.isRegularFile(envFile)) {
			return;
		}

		List<String> lines;
		try {
			lines = Files.readAllLines(envFile);
		} catch (IOException e) {
			return;
		}

		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty() || trimmed.startsWith("#")) {
				continue;
			}
			int idx = trimmed.indexOf('=');
			if (idx <= 0) {
				continue;
			}
			String key = trimmed.substring(0, idx).trim();
			String value = trimmed.substring(idx + 1).trim();
			if (System.getProperty(key) == null && System.getenv(key) == null) {
				System.setProperty(key, value);
			}
		}
	}

}
