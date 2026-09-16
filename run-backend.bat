@echo off
title Spring Boot Backend (Port 8080)
cd /d "%~dp0backend"
if "%MYSQL_PASSWORD%"=="" (
    set /p MYSQL_PASSWORD=Please enter your MySQL Password: 
)
echo Starting Fuel Inventory Spring Boot Backend with MySQL...
java -DMYSQL_PASSWORD="%MYSQL_PASSWORD%" -jar target/fuel-inventory-service-1.0.0.jar
pause
