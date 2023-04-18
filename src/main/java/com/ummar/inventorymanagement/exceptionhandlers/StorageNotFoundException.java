package com.ummar.inventorymanagement.exceptionhandlers;

public class StorageNotFoundException extends RuntimeException{
    public StorageNotFoundException(Long id) {
		super("Could not find storage " + id);
	}
}
