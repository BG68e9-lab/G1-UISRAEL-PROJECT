package com.uisrael.drinkhouse.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashPassword {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		String password = "admin123";
		String hash = encoder.encode(password);
		
		System.out.println("Password: " + password);
		System.out.println("Hash BCrypt: " + hash);
		System.out.println();
		
		boolean matches = encoder.matches(password, hash);
		System.out.println("¿El hash coincide?: " + matches);
		
		String hashActual = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
		boolean matchesActual = encoder.matches(password, hashActual);
		System.out.println("¿El hash actual de la BD coincide?: " + matchesActual);
	}
}
