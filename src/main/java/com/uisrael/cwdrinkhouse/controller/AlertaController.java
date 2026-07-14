package com.uisrael.cwdrinkhouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alerta")
public class AlertaController {

	@GetMapping
	public String leerPagina() {
		return "alertas/listaralertas";
	}
}
