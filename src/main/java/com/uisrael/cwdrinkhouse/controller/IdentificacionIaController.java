package com.uisrael.cwdrinkhouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/identificacionia")
public class IdentificacionIaController {

	@GetMapping
	public String leerPagina() {
		return "identificacionesia/listaridentificacionesia";
	}
}
