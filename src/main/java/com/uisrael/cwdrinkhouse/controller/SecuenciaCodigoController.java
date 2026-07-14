package com.uisrael.cwdrinkhouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secuenciacodigo")
public class SecuenciaCodigoController {

	@GetMapping
	public String leerPagina() {
		return "secuenciascodigo/listarsecuenciascodigo";
	}
}
