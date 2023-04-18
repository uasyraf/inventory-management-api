package com.ummar.inventorymanagement.repositories;

import java.util.List;

import com.ummar.inventorymanagement.models.Storage;
import com.ummar.inventorymanagement.models.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {
    List<Storage> findByTag(Tag tag);
}
