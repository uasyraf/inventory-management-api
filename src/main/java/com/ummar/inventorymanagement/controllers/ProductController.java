package com.ummar.inventorymanagement.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.models.Storage;
import com.ummar.inventorymanagement.models.Tag;
import com.ummar.inventorymanagement.assemblers.ProductModelAssembler;
import com.ummar.inventorymanagement.repositories.ProductRepository;
import com.ummar.inventorymanagement.repositories.StorageRepository;
import com.ummar.inventorymanagement.repositories.TagRepository;
import com.ummar.inventorymanagement.exceptionhandlers.GenericHttpResponseEntity;
import com.ummar.inventorymanagement.exceptionhandlers.ProductNotFoundException;
import com.ummar.inventorymanagement.exceptionhandlers.StorageNotFoundException;
import com.ummar.inventorymanagement.exceptionhandlers.TagNotFoundException;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@RestController
public class ProductController {

    private final ProductRepository repository;
    
    private final ProductModelAssembler assembler;
    
    private final TagRepository tagRepository;

    private final StorageRepository storageRepository;

    public ProductController(ProductRepository repository, ProductModelAssembler assembler, TagRepository tagRepository, StorageRepository storageRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.tagRepository = tagRepository;
        this.storageRepository = storageRepository;
    }

    // aggregate root
    @GetMapping("/products")
    public CollectionModel<EntityModel<Product>> all() {
        List<EntityModel<Product>> products = repository.findAll().stream() //
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(products, linkTo(methodOn(ProductController.class).all()).withSelfRel());
        
    }

    @Transactional
    @PostMapping("/products/storages/{storageId}")
    ResponseEntity<?> newProduct(@RequestBody Product newProduct, @PathVariable Long storageId) {
        GenericHttpResponseEntity<String> responseEntity = new GenericHttpResponseEntity<>();
        Optional<Storage> optionalStorage = storageRepository.findById(storageId);
        if (optionalStorage.isPresent()) {
            Storage storage = optionalStorage.get();
            newProduct.setStorage(storage);
            Product product = repository.save(newProduct);
            storage.getProducts().add(product);
            em.merge(storage);

            EntityModel<Product> entityModel = assembler.toModel(product);

            return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
        } else {

            return responseEntity.createResponse("There's no such storage by the id " + storageId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/products/{id}")
	public EntityModel<Product> one(@PathVariable Long id) {
		Product product = repository.findById(id) //
				.orElseThrow(() -> new ProductNotFoundException(id));

		return assembler.toModel(product);
	}

    @Transactional
    @PutMapping("/products/{id}")
    ResponseEntity<?> updateProduct(@RequestBody Product newProduct, @PathVariable Long id) {
        Product updatedProduct = repository.findById(id) //
            .map(product -> {
                product.setName(newProduct.getName());
                product.setSku(newProduct.getSku());
                product.setCategory(newProduct.getCategory());
                product.setQuantity(newProduct.getQuantity());
                product.setSupplier(newProduct.getSupplier());
                return repository.save(product);
            }) //
            .orElseGet(() -> {
                newProduct.setId(id);
                return repository.save(newProduct);
            }); 

        EntityModel<Product> entityModel = assembler.toModel(updatedProduct);

        return ResponseEntity //
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
            .body(entityModel);
    }

    @Transactional
    @PutMapping("/products/{id}/storages/{storageId}")
    ResponseEntity<?> updateProductStorage(@PathVariable Long id, @PathVariable Long storageId) {
        Product updatedProduct = repository.findById(id)
            .map(product -> {
                storageRepository.findById(storageId).map(newStorage -> {
                    if (product.getStorage() != null) {
                        Storage storage = product.getStorage();
                        storage.getProducts().remove(product);
                        em.merge(storage);
                    }
                    product.setStorage(newStorage);
                    newStorage.getProducts().add(product);
                    return em.merge(newStorage);
                }) //
                .orElseThrow(() -> new StorageNotFoundException(storageId));
                return repository.save(product);
            }) //
            .orElseThrow(() -> new ProductNotFoundException(id));

        EntityModel<Product> entityModel = assembler.toModel(updatedProduct);

        return ResponseEntity //
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
            .body(entityModel);
        }

    @Transactional
    @PutMapping("/products/{id}/tags/{tagId}")
    ResponseEntity<?> updateProductTag(@PathVariable Long id, @PathVariable Long tagId) {
        Product updatedProduct = repository.findById(id)
            .map(product -> {
                tagRepository.findById(tagId).map(newTag -> {
                    if (product.getTag() != null) {
                        Tag tag = product.getTag();
                        tag.getProducts().remove(product);
                        em.merge(tag);
                    }
                    product.setTag(newTag);
                    newTag.getProducts().add(product);
                    return em.merge(newTag);
                }) //
                .orElseThrow(() -> new TagNotFoundException(tagId));
                return repository.save(product);
            }) //
            .orElseThrow(() -> new ProductNotFoundException(id));

        EntityModel<Product> entityModel = assembler.toModel(updatedProduct);

        return ResponseEntity //
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
            .body(entityModel);
        }
        
    @DeleteMapping("/products/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        repository.deleteById(id);
        return new ResponseEntity<>("Product deleted successfully!", HttpStatus.OK);
    }
        
    @PersistenceContext
        private EntityManager em;
    }
        
        
        
        
        
        
