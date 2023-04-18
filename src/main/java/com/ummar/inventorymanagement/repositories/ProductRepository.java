package com.ummar.inventorymanagement.repositories;

import java.util.List;

import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.models.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTag(Tag tag);
}
