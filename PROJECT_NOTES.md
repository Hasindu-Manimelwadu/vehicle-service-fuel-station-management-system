# Integration notes

Package names intentionally use `com.sliit.vsfms` to make integration with the group VSFMS project easier.

Copy the following into the group project if needed:
- model/FuelSale.java
- model/PaymentMethod.java
- model/PaymentStatus.java
- repository/FuelSaleRepository.java
- service/FuelSaleService.java
- controller/FuelSaleController.java
- controller/ReportsController.java
- dashboard-related methods from controller/DashboardController.java
- templates/sales/*
- templates/reports/index.html

The `InventoryItem` model/repository in this standalone package is only a minimal dependency for demonstrating fuel stock reduction. In the full group project, keep the group's existing InventoryItem implementation instead of duplicating it.
