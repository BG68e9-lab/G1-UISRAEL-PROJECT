package com.uisrael.cwdrinkhouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/estadoreabasto")
public class EstadoReabastoController {

	@GetMapping
	public String leerPagina() {
		return "estadosreabasto/listarestadosreabasto";
	}
}
