# Suggested REST/Controller endpoints

## Fuel Sales
GET    /sales
GET    /sales/{id}
POST   /sales/save
GET    /sales/{id}/edit
POST   /sales/{id}/update
POST   /sales/{id}/delete

## Payments
GET    /payments
GET    /payments/{id}
POST   /payments/save
GET    /payments/{id}/edit
POST   /payments/{id}/update
POST   /payments/{id}/delete

## Reporting
GET    /reports
GET    /reports/sales
GET    /reports/sales?from=YYYY-MM-DD&to=YYYY-MM-DD
