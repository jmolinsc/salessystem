package com.salessystem.repository;

import com.salessystem.model.RolMenuItem;
import com.salessystem.model.RolMenuItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolMenuItemRepository extends JpaRepository<RolMenuItem, RolMenuItemId> {
    void deleteByRolIdAndMenuItemId(Long rolId, Long menuItemId);
    boolean existsByRolIdAndMenuItemId(Long rolId, Long menuItemId);
}