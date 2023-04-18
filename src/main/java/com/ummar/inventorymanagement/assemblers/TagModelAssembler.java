package com.ummar.inventorymanagement.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Iterator;
import java.util.Set;

import com.ummar.inventorymanagement.models.Tag;
import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.models.Storage;
import com.ummar.inventorymanagement.controllers.ProductController;
import com.ummar.inventorymanagement.controllers.StorageController;
import com.ummar.inventorymanagement.controllers.TagController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class TagModelAssembler implements RepresentationModelAssembler<Tag, EntityModel<Tag>>{

    @Override
    public EntityModel<Tag> toModel(Tag tag) {
        
        EntityModel<Tag> tagModel = EntityModel.of(tag,
            linkTo(methodOn(TagController.class).one(tag.getId())).withSelfRel(),
            linkTo(methodOn(TagController.class).all()).withRel("tags"));
        
        Set<Storage> storages = tag.getStorages();
        if (!storages.isEmpty()) {
            Iterator<Storage> storagesIterator = storages.iterator();
            while(storagesIterator.hasNext()) {
                Storage storage = storagesIterator.next();
                tagModel.add(linkTo(methodOn(StorageController.class).one(storage.getId())).withRel("storages"));
            }
        }
        
        Set<Product> products = tag.getProducts();
        if (!products.isEmpty()) {
            Iterator<Product> productsIterator = products.iterator();
            while(productsIterator.hasNext()) {
                Product product = productsIterator.next();
                tagModel.add(linkTo(methodOn(ProductController.class).one(product.getId())).withRel("products"));
            }
        }
        
        return tagModel;
    }
}
