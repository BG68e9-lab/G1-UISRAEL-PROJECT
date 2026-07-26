package com.uisrael.cwdrinkhouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movimientos")
public class MovimientoInventarioController {
	
	@GetMapping
	public String leerPagina() {
		return "movimientosinventario/listarmovimientoinventario";
	}
	
	/**
	 * Show movement creation form.
	 * GET /movimientos/new
	 */
	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("pageTitle", "Nuevo Movimiento de Inventario");
		// Placeholder - will be implemented with full movement creation logic
		return "movimientosinventario/form";
	}

}
