package com.sliit.vehicleservice.fuelinventory.service;

import com.sliit.vehicleservice.fuelinventory.dto.SparePartQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartResponseDTO;

import java.util.List;

public interface SparePartService {

    SparePartResponseDTO createSparePart(SparePartRequestDTO requestDTO);

    List<SparePartResponseDTO> getAllSpareParts(String search, String category, String status, Boolean lowStockOnly);

    SparePartResponseDTO getSparePartById(Long id);

    SparePartResponseDTO updateSparePart(Long id, SparePartRequestDTO requestDTO);

    void deleteSparePart(Long id);

    SparePartResponseDTO updateQuantity(Long id, SparePartQuantityUpdateDTO updateDTO);

    List<SparePartResponseDTO> getLowStockSpareParts();

    List<String> getAllCategories();
}