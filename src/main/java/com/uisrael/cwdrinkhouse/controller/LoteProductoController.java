package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.LotDTO;
import com.uisrael.cwdrinkhouse.service.LotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
// import org.springframework.security.access.prepost.PreAuthorize; // Replaced with SimpleAuth
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing product lots in the web UI.
 * Handles lot listing, filtering, and expiration monitoring.
 * 
 * Requirements: 6.1-6.7, 22
 */
@Controller
@RequestMapping("/lotes")
// @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')") // Replaced with SimpleAuth
public class LoteProductoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoteProductoController.class);

    @Autowired
    private LotService lotService;

    /**
     * Display all lots with pagination.
     * GET /lotes
     */
    @GetMapping
    public String listLots(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "25") int size,
                          @RequestParam(required = false) Long productoId,
                          @RequestParam(required = false) String search,
                          Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<LotDTO> lots;

            if (search != null && !search.trim().isEmpty()) {
                lots = lotService.searchLots(search.trim(), pageable);
                model.addAttribute("searchTerm", search.trim());
            } else if (productoId != null) {
                lots = lotService.getLotsByProduct(productoId, pageable);
                model.addAttribute("filteredByProduct", productoId);
            } else {
                lots = lotService.getAllLots(pageable);
            }

            model.addAttribute("lots", lots);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", lots.getTotalPages());
            model.addAttribute("totalElements", lots.getTotalElements());

            // Add expiring lots count for alerts
            long expiringCount = lotService.getExpiringLotsCount();
            model.addAttribute("expiringLotsCount", expiringCount);

            return "lotes/list";
            
        } catch (Exception e) {
            logger.error("Error listing lots", e);
            model.addAttribute("error", "Error al cargar los lotes");
            return "lotes/list";
        }
    }

    /**
     * Display lots expiring soon (within 7 days).
     * GET /lotes/expiring
     */
    @GetMapping("/expiring")
    public String listExpiringLots(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "25") int size,
                                  @RequestParam(defaultValue = "7") int days,
                                  Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<LotDTO> expiringLots = lotService.getLotsExpiringInDays(days, pageable);

            model.addAttribute("lots", expiringLots);
            model.addAttribute("expiringDays", days);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", expiringLots.getTotalPages());
            model.addAttribute("totalElements", expiringLots.getTotalElements());
            model.addAttribute("isExpiringView", true);

            return "lotes/list";
            
        } catch (Exception e) {
            logger.error("Error listing expiring lots", e);
            model.addAttribute("error", "Error al cargar los lotes próximos a vencer");
            return "lotes/list";
        }
    }

    /**
     * Display lots by product ID.
     * GET /lotes/producto/{productoId}
     */
    @GetMapping("/producto/{productoId}")
    public String listLotsByProduct(@PathVariable Long productoId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "25") int size,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<LotDTO> lots = lotService.getLotsByProduct(productoId, pageable);

            if (lots.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "No se encontraron lotes para este producto");
                return "redirect:/lotes";
            }

            model.addAttribute("lots", lots);
            model.addAttribute("filteredByProduct", productoId);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", lots.getTotalPages());
            model.addAttribute("totalElements", lots.getTotalElements());

            return "lotes/list";
            
        } catch (Exception e) {
            logger.error("Error listing lots for product {}", productoId, e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar los lotes del producto");
            return "redirect:/lotes";
        }
    }

    /**
     * Display lot details.
     * GET /lotes/{id}
     */
    @GetMapping("/{id}")
    public String viewLotDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<LotDTO> lotOptional = lotService.getLotById(id);

            if (lotOptional.isPresent()) {
                LotDTO lot = lotOptional.get();
                model.addAttribute("lot", lot);
                model.addAttribute("isExpiringSoon", lot.isExpiringSoon());
                model.addAttribute("isExpired", lot.isExpired());
                model.addAttribute("daysUntilExpiration", lot.getDaysUntilExpiration());
                model.addAttribute("consumedPercentage", lot.getConsumedPercentage());
                
                return "lotes/details";
            } else {
                redirectAttributes.addFlashAttribute("error", "Lote no encontrado");
                return "redirect:/lotes";
            }
            
        } catch (Exception e) {
            logger.error("Error viewing lot details for ID {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar los detalles del lote");
            return "redirect:/lotes";
        }
    }

    /**
     * Display lots with available stock.
     * GET /lotes/with-stock
     */
    @GetMapping("/with-stock")
    public String listLotsWithStock(@RequestParam(required = false) Long productoId, Model model) {
        try {
            List<LotDTO> lotsWithStock;

            if (productoId != null) {
                lotsWithStock = lotService.getLotsWithAvailableStock(productoId);
                model.addAttribute("filteredByProduct", productoId);
            } else {
                lotsWithStock = lotService.getLotsWithAvailableStock();
            }

            model.addAttribute("lots", lotsWithStock);
            model.addAttribute("isStockView", true);

            return "lotes/list";
            
        } catch (Exception e) {
            logger.error("Error listing lots with stock", e);
            model.addAttribute("error", "Error al cargar los lotes con stock disponible");
            return "lotes/list";
        }
    }

    /**
     * AJAX endpoint to get expiring lots count.
     * GET /lotes/expiring/count
     */
    @GetMapping("/expiring/count")
    @ResponseBody
    public Long getExpiringLotsCount(@RequestParam(defaultValue = "7") int days) {
        try {
            return lotService.getExpiringLotsCount(days);
        } catch (Exception e) {
            logger.error("Error getting expiring lots count", e);
            return 0L;
        }
    }

    /**
     * AJAX endpoint to get lots statistics.
     * GET /lotes/stats
     */
    @GetMapping("/stats")
    @ResponseBody
    public LotStats getLotStats() {
        try {
            return new LotStats(
                lotService.getTotalLotsCount(),
                lotService.getActiveLotsCount(),
                lotService.getLotsWithStockCount(),
                lotService.getExpiringLotsCount()
            );
        } catch (Exception e) {
            logger.error("Error getting lot statistics", e);
            return new LotStats(0L, 0L, 0L, 0L);
        }
    }

    /**
     * DTO for lot statistics response.
     */
    public static class LotStats {
        private final long totalLots;
        private final long activeLots;
        private final long lotsWithStock;
        private final long expiringLots;

        public LotStats(long totalLots, long activeLots, long lotsWithStock, long expiringLots) {
            this.totalLots = totalLots;
            this.activeLots = activeLots;
            this.lotsWithStock = lotsWithStock;
            this.expiringLots = expiringLots;
        }

        // Getters
        public long getTotalLots() { return totalLots; }
        public long getActiveLots() { return activeLots; }
        public long getLotsWithStock() { return lotsWithStock; }
        public long getExpiringLots() { return expiringLots; }
    }
}
