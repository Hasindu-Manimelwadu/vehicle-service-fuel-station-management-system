package com.sliit.vsfms.repository;

import com.sliit.vsfms.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository
        extends JpaRepository<InventoryItem, Long> {

}