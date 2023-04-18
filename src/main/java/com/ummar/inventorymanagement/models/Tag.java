package com.ummar.inventorymanagement.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;

@Entity
public class Tag {
    private @Id @GeneratedValue Long id;
    
    @Column(unique = true, nullable = false)
    private String name;

    @JsonManagedReference(value = "tag-products")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tag")
    private Set<Product> products = new HashSet<>();

    @JsonManagedReference(value = "tag-storages")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tag")
    private Set<Storage> storages = new HashSet<>();
    
    public Tag (){}

    Tag (String name, Set<Product> products, Set<Storage> storages){
        this.name = name;
        this.products = products;
        this.storages = storages;
    }

    public Long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Set<Product> getProducts() {
        return this.products;
    }

    public Set<Storage> getStorages() {
        return this.storages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void setStorages(Set<Storage> storages) {
        this.storages = storages;
    }
    
    public void addProduct(Product product) {
        this.products.add(product);
        product.setTag(this);
    }
    
    public void removeProduct(Product product) {
        this.products.remove(product);
        product.setTag(null);
    }

    public void addStorage(Storage storage) {
        this.storages.add(storage);
        storage.setTag(this);
    }

    public void removeStorage(Storage storage) {
        this.storages.remove(storage);
        storage.setTag(null);
    }

    // @Override
    // public boolean equals(Object o) {
    //     if (this == o)
    //         return true;
    //     if (!(o instanceof Tag))
    //         return false;
    //     Tag tag = (Tag) o;
    //     return Objects.equals(this.id, tag.id) && Objects.equals(this.name, tag.name) //
    //                 && Objects.equals(this.products, tag.products) && Objects.equals(this.storages, tag.storages);
    // }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(this.id, this.name, this.products, this.storages);
    // }

    // @Override
    // public String toString() {
    //     return "Tag{" + "id=" + this.id + ", name='" + this.name + '\'' + "}";
    // }
}
