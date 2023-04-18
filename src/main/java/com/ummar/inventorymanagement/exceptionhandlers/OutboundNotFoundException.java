package com.ummar.inventorymanagement.exceptionhandlers;

public class OutboundNotFoundException extends RuntimeException{
    public OutboundNotFoundException(Long id) {
		super("Could not find outbound " + id);
	}

    public OutboundNotFoundException(String reference) {
		super("Could not find outbound " + reference);
	}
}
