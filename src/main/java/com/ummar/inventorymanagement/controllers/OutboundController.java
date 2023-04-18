package com.ummar.inventorymanagement.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.assemblers.OutboundModelAssembler;
import com.ummar.inventorymanagement.exceptionhandlers.GenericHttpResponseEntity;
import com.ummar.inventorymanagement.exceptionhandlers.OutboundNotFoundException;
import com.ummar.inventorymanagement.models.Outbound;
import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.repositories.OutboundRepository;
import com.ummar.inventorymanagement.repositories.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutboundController {

    private final OutboundRepository repository;
    
    private final OutboundModelAssembler assembler;
    
    private final ProductRepository productRepository;

    public OutboundController(OutboundRepository repository, OutboundModelAssembler assembler, ProductRepository productRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.productRepository = productRepository;
    }

    // aggregate root
    @GetMapping("/outbounds")
    public CollectionModel<EntityModel<Outbound>> all() {
        List<EntityModel<Outbound>> outbounds = repository.findAll().stream() //
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(outbounds, linkTo(methodOn(OutboundController.class).all()).withSelfRel());
    }

    @Transactional
    @PostMapping("/outbounds/products/{productId}")
    ResponseEntity<?> newOutbound(@RequestBody Outbound newOutbound, @PathVariable Long productId) {
        GenericHttpResponseEntity<String> responseEntity = new GenericHttpResponseEntity<>();
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Long newQuantity = product.getQuantity() - newOutbound.getQuantity();
            if (newQuantity < 0) {
                return responseEntity.createResponse("Outbound quantity should not be more than product quantity!", HttpStatus.BAD_REQUEST);
            } else {
                product.setQuantity(newQuantity);
                em.merge(product);
                newOutbound.setProduct(product);
                Outbound outbound = repository.save(newOutbound);
                EntityModel<Outbound> entityModel = assembler.toModel(outbound);
                return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);
            }
        } else {

            return responseEntity.createResponse("There's no such product by the id " + productId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/outbounds/{id}")
	public EntityModel<Outbound> one(@PathVariable Long id) {
		Outbound outbound = repository.findById(id) //
				.orElseThrow(() -> new OutboundNotFoundException(id));

		return assembler.toModel(outbound);
	}

    // @DeleteMapping("/outbounds/{id}")
    // ResponseEntity<?> deleteOutbound(@PathVariable Long id) {
    //     repository.deleteById(id);
    //     return ResponseEntity.noContent().build();
    // }

    @PersistenceContext
    private EntityManager em;
}
