package com.martingago.words;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class WordsApplication {

	public static void main(String[] args) {
		if(Files.exists(Paths.get(".env"))){
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		}
		SpringApplication.run(WordsApplication.class, args);
	}

}
