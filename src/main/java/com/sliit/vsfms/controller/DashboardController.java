package com.sliit.vsfms.controller;

import com.sliit.vsfms.model.InventoryItem;
import com.sliit.vsfms.service.FuelSaleService;
import com.sliit.vsfms.repository.InventoryItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {
    private final FuelSaleService sales;
    private final InventoryItemRepository inventory;
    public DashboardController(FuelSaleService sales, InventoryItemRepository inventory){this.sales=sales;this.inventory=inventory;}
    @GetMapping({"/","/dashboard"})
    public String dashboard(Model model){
        List<InventoryItem> low=inventory.findAll().stream().filter(InventoryItem::isLowStock).toList();
        model.addAttribute("paidRevenue",sales.paidRevenue());
        model.addAttribute("paidCount",sales.paidCount());
        model.addAttribute("paidQuantity",sales.paidQuantity());
        model.addAttribute("totalSales",sales.findAll().size());
        model.addAttribute("recentSales",sales.recent());
        model.addAttribute("lowStock",low);
        return "dashboard";
    }
}
