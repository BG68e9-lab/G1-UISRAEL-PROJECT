package com.uisrael.cwdrinkhouseco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/producto")
public class ProductoController {
	
	@GetMapping
	public String leerPagina() {
		
		return "productos/listarproducto";
	}
}
