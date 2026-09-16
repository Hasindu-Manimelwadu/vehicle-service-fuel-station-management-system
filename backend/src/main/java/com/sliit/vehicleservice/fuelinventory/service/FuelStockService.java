package com.sliit.vehicleservice.fuelinventory.service;

import com.sliit.vehicleservice.fuelinventory.dto.FuelQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockResponseDTO;
import com.sliit.vehicleservice.fuelinventory.dto.PriceUpdateDTO;

import java.util.List;

public interface FuelStockService {

    FuelStockResponseDTO createFuelStock(FuelStockRequestDTO requestDTO);

    List<FuelStockResponseDTO> getAllFuelStocks(String search, Boolean lowStockOnly, String tankNo);

    FuelStockResponseDTO getFuelStockById(Long id);

    FuelStockResponseDTO updateFuelStock(Long id, FuelStockRequestDTO requestDTO);

    void deleteFuelStock(Long id);

    FuelStockResponseDTO updateQuantity(Long id, FuelQuantityUpdateDTO updateDTO);

    FuelStockResponseDTO updatePrice(Long id, PriceUpdateDTO priceUpdateDTO);

    List<FuelStockResponseDTO> getLowStockFuelStocks();
}