package com.ummar.inventorymanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ummar.inventorymanagement.models.Inbound;

public interface InboundRepository extends JpaRepository<Inbound, Long> {

}
