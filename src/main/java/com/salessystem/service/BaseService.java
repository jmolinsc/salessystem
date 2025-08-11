package com.salessystem.service;

import java.util.List;
import java.util.Optional;

public interface BaseService<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    T save(T entity);
    void deleteById(Long id);
    List<T> search(String query);
}