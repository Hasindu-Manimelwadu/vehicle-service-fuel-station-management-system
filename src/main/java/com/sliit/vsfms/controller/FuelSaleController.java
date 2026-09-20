package com.sliit.vsfms.controller;

import com.sliit.vsfms.model.FuelSale;
import com.sliit.vsfms.repository.InventoryItemRepository;
import com.sliit.vsfms.service.FuelSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sales")
public class FuelSaleController {

    private final FuelSaleService fuelSaleService;
    private final InventoryItemRepository inventoryItemRepository;

    public FuelSaleController(
            FuelSaleService fuelSaleService,
            InventoryItemRepository inventoryItemRepository) {

        this.fuelSaleService = fuelSaleService;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @GetMapping
    public String getAllSales(Model model) {

        List<FuelSale> sales =
                fuelSaleService.getAllSales();

        model.addAttribute("sales", sales);

        return "sales/list";
    }

    @GetMapping("/{id}")
    public String getSaleById(
            @PathVariable Long id,
            Model model) {

        FuelSale sale =
                fuelSaleService.getSaleById(id);

        model.addAttribute("sale", sale);

        return "sales/invoice";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        FuelSale sale =
                fuelSaleService.getSaleById(id);

        model.addAttribute("sale", sale);

        model.addAttribute(
                "inventoryItems",
                inventoryItemRepository.findAll()
        );

        return "sales/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateSale(
            @PathVariable Long id,
            @RequestParam Long inventoryItemId,
            @RequestParam Double quantity,
            @RequestParam(required = false)
            String vehicleNumber,
            @RequestParam(required = false)
            String customerName) {

        fuelSaleService.updateSale(
                id,
                inventoryItemId,
                quantity,
                vehicleNumber,
                customerName
        );

        return "redirect:/sales";
    }
}