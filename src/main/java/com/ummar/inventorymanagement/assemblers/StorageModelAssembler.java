package com.ummar.inventorymanagement.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Iterator;
import java.util.Set;

import com.ummar.inventorymanagement.models.Storage;
import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.controllers.ProductController;
import com.ummar.inventorymanagement.controllers.StorageController;
import com.ummar.inventorymanagement.controllers.TagController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class StorageModelAssembler implements RepresentationModelAssembler<Storage, EntityModel<Storage>>{

    @Override
    public EntityModel<Storage> toModel(Storage storage) {

        EntityModel<Storage> storageModel = EntityModel.of(storage, //
            linkTo(methodOn(StorageController.class).one(storage.getId())).withSelfRel(),
            linkTo(methodOn(StorageController.class).all()).withRel("storages"));

        Set<Product> products = storage.getProducts();
        if (!products.isEmpty()) {
            Iterator<Product> productsIterator = products.iterator();
            while(productsIterator.hasNext()) {
                Product product = productsIterator.next();
                storageModel.add(linkTo(methodOn(ProductController.class).one(product.getId())).withRel("products"));
            }
        }
        
        if (storage.getTag() != null) {
            storageModel.add(linkTo(methodOn(TagController.class).one(storage.getTag().getId())).withRel("tag"));
        }

        return storageModel;
    }
}
