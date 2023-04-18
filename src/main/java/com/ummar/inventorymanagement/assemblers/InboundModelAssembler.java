package com.ummar.inventorymanagement.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.models.Inbound;
import com.ummar.inventorymanagement.controllers.InboundController;
import com.ummar.inventorymanagement.controllers.ProductController;
import com.ummar.inventorymanagement.controllers.StorageController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class InboundModelAssembler implements RepresentationModelAssembler<Inbound, EntityModel<Inbound>>{

    @Override
    public EntityModel<Inbound> toModel(Inbound inbound) {
        
        EntityModel<Inbound> InboundModel = EntityModel.of(inbound,
            linkTo(methodOn(InboundController.class).one(inbound.getId())).withSelfRel(),
            linkTo(methodOn(InboundController.class).all()).withRel("inbounds"),
            linkTo(methodOn(ProductController.class).one(inbound.getProduct().getId())).withRel("product"),
            linkTo(methodOn(StorageController.class).one(inbound.getStorage().getId())).withRel("storage"));
        
        return InboundModel;
    }
}
