package com.ummar.inventorymanagement.controllers;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.models.*;
import com.ummar.inventorymanagement.assemblers.*;
import com.ummar.inventorymanagement.repositories.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.ummar.inventorymanagement.exceptionhandlers.TagNotFoundException;

import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagController {

    private final TagRepository repository;
    
    private final TagModelAssembler assembler;

    public TagController(TagRepository repository, TagModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // aggregate root
    @GetMapping("/tags")
    public CollectionModel<EntityModel<Tag>> all() {
        List<EntityModel<Tag>> tags = repository.findAll().stream() //
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(tags, linkTo(methodOn(TagController.class).all()).withSelfRel());
        
    }

    @GetMapping("/search/tags")
    public ResponseEntity<?> searchByName(@RequestParam("name") String name) {
        Optional<Tag> optionalTag = repository.findByName(name);
        if (optionalTag.isPresent()) {
            EntityModel<Tag> entityModel = assembler.toModel(optionalTag.get());
            return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
        } else {
            return new ResponseEntity<>("There's no such tag by name '" + name + "'", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tags")
    EntityModel<Tag> newTag(@RequestBody Tag newTag) {
        Tag tag = repository.save(newTag);
        return assembler.toModel(tag);
    }


    @GetMapping("/tags/{id}")
	public EntityModel<Tag> one(@PathVariable Long id) {
		Tag tag = repository.findById(id) //
				.orElseThrow(() -> new TagNotFoundException(id));

		return assembler.toModel(tag);
	}

    @PutMapping("/tags/{id}")
    ResponseEntity<?> updateTag(@RequestBody Tag newTag, @PathVariable Long id) {
        EntityModel<Tag> entityModel = repository.findById(id) //
            .map(tag -> {
                    tag.setName(newTag.getName());
                    return assembler.toModel(repository.save(tag));
            }) //
            .orElseGet(() -> {
                    newTag.setId(id);
                    newTag.setProducts(new HashSet<>());
                    newTag.setStorages(new HashSet<>());
                    return assembler.toModel(repository.save(newTag));
            });
        return ResponseEntity //
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
            .body(entityModel);
    }

    @Transactional
    @DeleteMapping("/tags/{id}")
	ResponseEntity<?> deleteTag(@PathVariable Long id) {
		Tag tag = repository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
        for (Product product : tag.getProducts()) {
            product.removeTag();
            em.merge(product);
        }
        for (Storage storage : tag.getStorages()) {
            storage.removeTag();
            em.merge(storage);
        }
        repository.delete(tag);
        return new ResponseEntity<>("Tag deleted successfully!", HttpStatus.OK);
	}

    @PersistenceContext
    private EntityManager em;
}
