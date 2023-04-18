package com.ummar.inventorymanagement.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.models.Product;
import com.ummar.inventorymanagement.controllers.ProductController;
import com.ummar.inventorymanagement.controllers.StorageController;
import com.ummar.inventorymanagement.controllers.TagController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class ProductModelAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>>{

    @Override
    public EntityModel<Product> toModel(Product product) {

        EntityModel<Product> productModel = EntityModel.of(product, //
            linkTo(methodOn(ProductController.class).one(product.getId())).withSelfRel(),
            linkTo(methodOn(ProductController.class).all()).withRel("products"));
            
        if (product.getStorage() != null) {
            productModel.add(linkTo(methodOn(StorageController.class).one(product.getStorage().getId())).withRel("storage"));
        }

        if (product.getTag() != null) {
            productModel.add(linkTo(methodOn(TagController.class).one(product.getTag().getId())).withRel("tag"));
        }

        return productModel;
    }
}
