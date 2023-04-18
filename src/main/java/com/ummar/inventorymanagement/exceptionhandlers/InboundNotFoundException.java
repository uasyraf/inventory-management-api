package com.ummar.inventorymanagement.exceptionhandlers;

public class InboundNotFoundException extends RuntimeException{
    public InboundNotFoundException(Long id) {
		super("Could not find inbound " + id);
	}

    public InboundNotFoundException(String reference) {
		super("Could not find inbound " + reference);
	}
}
