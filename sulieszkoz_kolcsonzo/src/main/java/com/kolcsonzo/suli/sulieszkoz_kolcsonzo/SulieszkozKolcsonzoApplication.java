package com.kolcsonzo.suli.sulieszkoz_kolcsonzo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SulieszkozKolcsonzoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SulieszkozKolcsonzoApplication.class, args);
	}

}
