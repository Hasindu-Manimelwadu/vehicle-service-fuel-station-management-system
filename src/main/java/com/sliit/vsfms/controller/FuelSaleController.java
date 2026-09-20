package com.sliit.vsfms.controller;

import com.sliit.vsfms.model.FuelSale;
import com.sliit.vsfms.service.FuelSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sales")
public class FuelSaleController {

    private final FuelSaleService fuelSaleService;

    public FuelSaleController(FuelSaleService fuelSaleService) {
        this.fuelSaleService = fuelSaleService;
    }

    @GetMapping
    public String getAllSales(Model model) {

        List<FuelSale> sales = fuelSaleService.getAllSales();

        model.addAttribute("sales", sales);

        return "sales/list";
    }

    @GetMapping("/{id}")
    public String getSaleById(@PathVariable Long id,
                              Model model) {

        FuelSale sale = fuelSaleService.getSaleById(id);

        model.addAttribute("sale", sale);

        return "sales/invoice";
    }
}