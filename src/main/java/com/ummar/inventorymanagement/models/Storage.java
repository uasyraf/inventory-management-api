package com.ummar.inventorymanagement.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Storage {
    private @Id @GeneratedValue Long id;

    @Column(unique = true, nullable = false) 
    private String name;
    
    private String address;

    @JsonManagedReference("storage-products")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "storage")
    private Set<Product> products = new HashSet<>();

    @ManyToOne
    @JsonBackReference(value = "tag-storages")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    Storage() {}

    Storage(String name, String address, Set<Product> products, Tag tag) {
        this.name = name;
        this.address = address;
        this.products = products;
        this.tag = tag;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void removeTag() {
        this.tag = null;
    }

    // @Override
    // public boolean equals(Object o) {
    //     if (this == o)
    //         return true;
    //     if (!(o instanceof Storage))
    //         return false;
    //     Storage storage = (Storage) o;
    //     return Objects.equals(this.id, storage.id) && Objects.equals(this.name, storage.name) //
    //                 && Objects.equals(this.address, storage.address) && Objects.equals(this.tag, storage.tag);
    // }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(this.id,
    //                 this.name,
    //                 this.address,
    //                 this.tag);
    // }

    // @Override
    // public String toString() {
    //     return "Storage{" + "id=" + this.id + ", name='" + this.name + '\'' + ", address='" + this.address + '\'' + "}";
    // }
}
