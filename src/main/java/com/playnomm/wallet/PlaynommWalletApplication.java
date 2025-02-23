package com.playnomm.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class PlaynommWalletApplication {
	public static void main (String[] args) {
		TimeZone.setDefault (TimeZone.getTimeZone ("UTC"));
		SpringApplication.run (PlaynommWalletApplication.class, args);
	}

}
