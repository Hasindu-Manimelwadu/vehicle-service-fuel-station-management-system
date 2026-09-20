package com.sliit.vsfms.controller;

import com.sliit.vsfms.model.*;
import com.sliit.vsfms.service.FuelSaleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales")
public class FuelSaleController {
    private final FuelSaleService service;
    public FuelSaleController(FuelSaleService service){this.service=service;}
    @GetMapping public String list(Model model){model.addAttribute("sales",service.findAll());return "sales/list";}
    @GetMapping("/new") public String form(Model model){load(model,new FuelSale());return "sales/form";}
    @PostMapping("/save") public String save(@Valid @ModelAttribute("sale") FuelSale sale, BindingResult result, Model model, RedirectAttributes redirect){
        if(result.hasErrors()){load(model,sale);return "sales/form";}
        try{FuelSale saved=service.create(sale);redirect.addFlashAttribute("success","Fuel sale recorded and payment/invoice generated successfully.");return "redirect:/sales/"+saved.getId()+"/invoice";}
        catch(IllegalArgumentException e){model.addAttribute("saleError",e.getMessage());load(model,sale);return "sales/form";}
    }
    @GetMapping("/{id}/invoice") public String invoice(@PathVariable Long id,Model model){model.addAttribute("sale",service.findById(id));return "sales/invoice";}
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes redirect){service.delete(id);redirect.addFlashAttribute("success","Sale deleted and the fuel quantity was restored.");return "redirect:/sales";}
    private void load(Model model,FuelSale sale){model.addAttribute("sale",sale);model.addAttribute("fuelItems",service.fuels());model.addAttribute("paymentMethods",PaymentMethod.values());model.addAttribute("paymentStatuses",PaymentStatus.values());}
}
