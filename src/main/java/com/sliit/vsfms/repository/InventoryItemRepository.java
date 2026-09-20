package com.sliit.vsfms.repository;
import com.sliit.vsfms.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findAllByItemTypeOrderByNameAsc(InventoryItemType itemType);
}
