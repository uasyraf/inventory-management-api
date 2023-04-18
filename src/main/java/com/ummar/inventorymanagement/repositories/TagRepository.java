package com.ummar.inventorymanagement.repositories;

import com.ummar.inventorymanagement.models.Tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
