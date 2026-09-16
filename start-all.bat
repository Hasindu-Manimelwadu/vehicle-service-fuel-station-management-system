@echo off
title Vehicle Service & Inventory Management Launcher
echo ======================================================================
echo    Vehicle Service and Fuel Inventory Management System
echo ======================================================================
if "%MYSQL_PASSWORD%"=="" (
    set /p MYSQL_PASSWORD=Please enter your MySQL Password: 
)
echo.
echo [1/2] Launching Spring Boot Backend (Port 8080) with MySQL...
start "Spring Boot Backend (Port 8080)" cmd /k "cd /d ""%~dp0backend"" && java -DMYSQL_PASSWORD=""%MYSQL_PASSWORD%"" -jar target/fuel-inventory-service-1.0.0.jar"

echo Waiting for backend initialization...
timeout /t 4 /nobreak >nul

echo [2/2] Launching React Vite Frontend (Port 5173)...
start "React Frontend (Port 5173)" cmd /k "cd /d ""%~dp0frontend"" && npm run dev"

echo.
echo ======================================================================
echo Both servers are starting up in separate windows!
echo - Frontend: http://localhost:5173/
echo - Backend API: http://localhost:8080/api/v1
echo - Backend Health: http://localhost:8080/actuator/health
echo ======================================================================
echo To stop the servers later, simply close the two command windows.
echo You can close this window now.
pause
