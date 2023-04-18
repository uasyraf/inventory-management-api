package com.ummar.inventorymanagement.controllers;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.models.Storage;
import com.ummar.inventorymanagement.models.Tag;
import com.ummar.inventorymanagement.assemblers.StorageModelAssembler;
import com.ummar.inventorymanagement.repositories.StorageRepository;
import com.ummar.inventorymanagement.repositories.TagRepository;
import com.ummar.inventorymanagement.exceptionhandlers.StorageNotFoundException;
import com.ummar.inventorymanagement.exceptionhandlers.TagNotFoundException;

import java.util.List;
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
public class StorageController {

    private final StorageRepository repository;
    
    private final StorageModelAssembler assembler;
    
    private final TagRepository tagRepository;

    public StorageController(StorageRepository repository, StorageModelAssembler assembler, TagRepository tagRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.tagRepository = tagRepository;
    }

    // aggregate root
    @GetMapping("/storages")
    public CollectionModel<EntityModel<Storage>> all() {
        List<EntityModel<Storage>> storages = repository.findAll().stream() //
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(storages, linkTo(methodOn(StorageController.class).all()).withSelfRel());
        
    }

    @PostMapping("/storages")
    Storage newStorage(@RequestBody Storage newStorage) {

        return repository.save(newStorage);
    }

    @GetMapping("/storages/{id}")
	public EntityModel<Storage> one(@PathVariable Long id) {
		Storage storage = repository.findById(id) //
				.orElseThrow(() -> new StorageNotFoundException(id));

		return assembler.toModel(storage);
	}

    @Transactional
    @PutMapping("/storages/{id}")
    ResponseEntity<?> updateStorage(@RequestBody Storage newStorage, @PathVariable Long id) {
        Storage updatedStorage = repository.findById(id) //
            .map(storage -> {
                storage.setName(newStorage.getName());
                storage.setAddress(newStorage.getAddress());
                return repository.save(storage);
            }) //
            .orElseGet(() -> {
                newStorage.setId(id);
                return repository.save(newStorage);
            }); 

        EntityModel<Storage> entityModel = assembler.toModel(updatedStorage);

        return ResponseEntity //
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
            .body(entityModel);
    }

    @Transactional
    @PutMapping("/storages/{id}/tags/{tagId}")
    ResponseEntity<?> updateStorageTag(@PathVariable Long id, @PathVariable Long tagId) {
        Storage updatedStorage = repository.findById(id)
            .map(storage -> {
                tagRepository.findById(tagId).map(newTag -> {
                    if (storage.getTag() != null) {
                        Tag tag = storage.getTag();
                        tag.getStorages().remove(storage);
                        em.merge(tag);
                    }
                    storage.setTag(newTag);
                    newTag.getStorages().add(storage);
                    return em.merge(newTag);
                }) //
                .orElseThrow(() -> new TagNotFoundException(tagId));
                return repository.save(storage);
            }) //
            .orElseThrow(() -> new StorageNotFoundException(id));

        EntityModel<Storage> entityModel = assembler.toModel(updatedStorage);

        return ResponseEntity //
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
            .body(entityModel);
    }

    @DeleteMapping("/storages/{id}")
	ResponseEntity<String> deleteStorage(@PathVariable Long id) {
		repository.deleteById(id);
        return new ResponseEntity<>("Storage deleted successfully!", HttpStatus.OK);
	}

    @PersistenceContext
    private EntityManager em;
}
