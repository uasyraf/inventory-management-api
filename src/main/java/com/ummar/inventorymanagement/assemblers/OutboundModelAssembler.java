package com.ummar.inventorymanagement.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.ummar.inventorymanagement.models.Outbound;
import com.ummar.inventorymanagement.controllers.ProductController;
import com.ummar.inventorymanagement.controllers.OutboundController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class OutboundModelAssembler implements RepresentationModelAssembler<Outbound, EntityModel<Outbound>>{

    @Override
    public EntityModel<Outbound> toModel(Outbound outbound) {
        
        EntityModel<Outbound> outboundModel = EntityModel.of(outbound,
            linkTo(methodOn(OutboundController.class).one(outbound.getId())).withSelfRel(),
            linkTo(methodOn(OutboundController.class).all()).withRel("outbounds"),
            linkTo(methodOn(ProductController.class).one(outbound.getProduct().getId())).withRel("product"));
        
        return outboundModel;
    }
}
