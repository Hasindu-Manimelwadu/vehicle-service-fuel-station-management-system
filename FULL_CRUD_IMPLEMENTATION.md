# Fuel Sales, Payment & Reporting Dashboard — Full CRUD

This module is the assigned VSFMS component for:
**Fuel Sales, Payment & Reporting Dashboard**

## CRUD coverage

### Fuel Sales
- Create: record a fuel sale
- Read: list sales and view invoice
- Update: edit a recorded sale
- Delete: remove a sale
- Search/filter: customer, fuel type, date range

### Payments
- Create: record payment for a sale
- Read: view payment status/history
- Update: change payment method/status/amount when permitted
- Delete: remove an incorrect payment record

### Invoices
- Generate invoice from a sale
- View invoice details

### Reporting Dashboard
- Total sales
- Total revenue
- Paid/pending totals
- Fuel quantity sold
- Date-range sales report
- Sales history

## Integration
Keep entity/repository/service/controller package names consistent with the main VSFMS project.
The module is designed to be merged into the team's shared Spring Boot project.
