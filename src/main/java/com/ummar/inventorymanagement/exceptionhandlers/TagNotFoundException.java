package com.ummar.inventorymanagement.exceptionhandlers;

public class TagNotFoundException extends RuntimeException{
    public TagNotFoundException(Long id) {
		super("Could not find tag " + id);
	}

    public TagNotFoundException(String name) {
		super("Could not find tag " + name);
	}
}
