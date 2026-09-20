package com.sliit.vsfms;

import com.sliit.vsfms.model.InventoryItem;
import com.sliit.vsfms.model.InventoryItemType;
import com.sliit.vsfms.repository.InventoryItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class FuelSalesPaymentReportingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FuelSalesPaymentReportingApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(InventoryItemRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(fuel("PET-92", "Petrol 92", "L", "350.00", "500.00", "2500.00"));
                repository.save(fuel("PET-95", "Petrol 95", "L", "370.00", "300.00", "500.00"));
                repository.save(fuel("DSL", "Auto Diesel", "L", "360.00", "800.00", "1000.00"));
                repository.save(fuel("SDSL", "Super Diesel", "L", "420.00", "180.00", "300.00"));
            }
        };
    }

    private InventoryItem fuel(String sku, String name, String unit, String price, String quantity, String reorder) {
        InventoryItem item = new InventoryItem();
        item.setSku(sku);
        item.setName(name);
        item.setItemType(InventoryItemType.FUEL);
        item.setUnit(unit);
        item.setUnitPrice(new BigDecimal(price));
        item.setQuantity(new BigDecimal(quantity));
        item.setReorderLevel(new BigDecimal(reorder));
        return item;
    }
}
