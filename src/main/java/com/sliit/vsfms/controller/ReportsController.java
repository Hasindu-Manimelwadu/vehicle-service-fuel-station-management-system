package com.sliit.vsfms.controller;

import com.sliit.vsfms.model.*;
import com.sliit.vsfms.service.FuelSaleService;
import com.sliit.vsfms.repository.FuelSaleRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportsController {
    private final FuelSaleService sales;
    private final FuelSaleRepository repository;
    public ReportsController(FuelSaleService sales,FuelSaleRepository repository){this.sales=sales;this.repository=repository;}
    @GetMapping
    public String reports(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                          @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to, Model model){
        if(from==null) from=LocalDate.now().withDayOfMonth(1);
        if(to==null) to=LocalDate.now();
        List<FuelSale> filtered=sales.between(from,to);
        BigDecimal revenue=filtered.stream().filter(s->s.getPaymentStatus()==PaymentStatus.PAID).map(FuelSale::getTotalAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal quantity=filtered.stream().filter(s->s.getPaymentStatus()==PaymentStatus.PAID).map(FuelSale::getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
        long paid=filtered.stream().filter(s->s.getPaymentStatus()==PaymentStatus.PAID).count();
        Map<PaymentMethod,Long> paymentCounts=filtered.stream().collect(Collectors.groupingBy(FuelSale::getPaymentMethod,Collectors.counting()));
        model.addAttribute("from",from);model.addAttribute("to",to);model.addAttribute("sales",filtered);model.addAttribute("revenue",revenue);model.addAttribute("quantity",quantity);model.addAttribute("paidCount",paid);model.addAttribute("paymentCounts",paymentCounts);model.addAttribute("lowStock",sales.fuels().stream().filter(InventoryItem::isLowStock).collect(Collectors.toList()));
        return "reports/index";
    }
}
