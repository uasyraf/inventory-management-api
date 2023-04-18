package com.ummar.inventorymanagement.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.assemblers.InboundModelAssembler;
import com.ummar.inventorymanagement.exceptionhandlers.GenericHttpResponseEntity;
import com.ummar.inventorymanagement.exceptionhandlers.InboundNotFoundException;
import com.ummar.inventorymanagement.models.Inbound;
import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.models.Storage;
import com.ummar.inventorymanagement.repositories.InboundRepository;
import com.ummar.inventorymanagement.repositories.ProductRepository;
import com.ummar.inventorymanagement.repositories.StorageRepository;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InboundController {

    private final InboundRepository repository;
    
    private final InboundModelAssembler assembler;
    
    private final ProductRepository productRepository;

    private final StorageRepository storageRepository;

    public InboundController(InboundRepository repository, InboundModelAssembler assembler, ProductRepository productRepository, StorageRepository storageRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.productRepository = productRepository;
        this.storageRepository = storageRepository;
    }

    // aggregate root
    @GetMapping("/inbounds")
    public CollectionModel<EntityModel<Inbound>> all() {
        List<EntityModel<Inbound>> inbounds = repository.findAll().stream() //
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(inbounds, linkTo(methodOn(InboundController.class).all()).withSelfRel());
    }

    @PostMapping("/inbounds/products/{productId}/storages/{storageId}")
    ResponseEntity<?> newInbound(@RequestBody Inbound newInbound, @PathVariable(name = "productId") Long productId, @PathVariable(name = "storageId") Long storageId) {
        GenericHttpResponseEntity<String> responseEntity = new GenericHttpResponseEntity<>();
        Optional<Storage> optionalStorage = storageRepository.findById(storageId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent() && optionalStorage.isPresent()) {
            Product product = optionalProduct.get();
            Storage storage = optionalStorage.get();
            Long newQuantity = product.getQuantity() + newInbound.getQuantity();
            if (newQuantity == product.getQuantity() || newQuantity < 0) {
                return responseEntity.createResponse("Inbound quantity should not be zero or negative!", HttpStatus.BAD_REQUEST);
            } else {
                product.setQuantity(newQuantity);
                product.setStorage(storage);
                productRepository.save(product);

                storage.getProducts().add(product);
                storageRepository.save(storage);

                newInbound.setProduct(product);
                newInbound.setStorage(storage);
                Inbound outbound = repository.save(newInbound);

                EntityModel<Inbound> entityModel = assembler.toModel(outbound);

                return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);
            }
        } else {
            if (!optionalProduct.isPresent() && !optionalStorage.isPresent()) {
                return responseEntity.createResponse("There's no such product by the id " + productId + " and storage by the id " + storageId, HttpStatus.NOT_FOUND);
            } else if (!optionalProduct.isPresent()){
                return responseEntity.createResponse("There's no such product by the id " + productId, HttpStatus.NOT_FOUND);
            } else {
                return responseEntity.createResponse("There's no such storage by the id " + storageId, HttpStatus.NOT_FOUND);
            }
        }
    }

    @GetMapping("/inbounds/{id}")
	public EntityModel<Inbound> one(@PathVariable Long id) {
		Inbound inbound = repository.findById(id) //
				.orElseThrow(() -> new InboundNotFoundException(id));

		return assembler.toModel(inbound);
	}
    
    @PersistenceContext
    private EntityManager em;
}
