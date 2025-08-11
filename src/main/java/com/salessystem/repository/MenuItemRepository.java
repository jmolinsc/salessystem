package com.salessystem.repository;

import com.salessystem.model.MenuItem;
import com.salessystem.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT DISTINCT mi FROM MenuItem mi JOIN mi.roles rm WHERE rm.rol IN :roles AND mi.menuPadre IS NULL ORDER BY mi.orden ASC")
    List<MenuItem> findMenuItemsByRoles(@Param("roles") Set<Rol> roles);

    @Query("SELECT DISTINCT mi FROM MenuItem mi JOIN mi.roles rm WHERE rm.rol IN :roles AND mi.menuPadre.id = :parentId ORDER BY mi.orden ASC")
    List<MenuItem> findSubMenuItemsByRolesAndParentId(@Param("roles") Set<Rol> roles, @Param("parentId") Long parentId);

    List<MenuItem> findByMenuPadreIsNullOrderByOrdenAsc();
}