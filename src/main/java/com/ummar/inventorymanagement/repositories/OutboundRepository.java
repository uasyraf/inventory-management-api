package com.ummar.inventorymanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ummar.inventorymanagement.models.Outbound;

public interface OutboundRepository extends JpaRepository<Outbound, Long> {

}
