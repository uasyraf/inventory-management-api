package com.ummar.inventorymanagement.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {
    private @Id @GeneratedValue Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String sku;
    
    private String category;

    @ManyToOne
    @JsonBackReference(value = "storage-products")
    @JoinColumn(name = "storage_id")
    private Storage storage;

    private Long quantity;
    private String supplier;

    @ManyToOne
    @JsonBackReference(value = "tag-products")
    @JoinColumn(name = "tag_id")
    private Tag tag;
    
    public Product() {}
    
    Product(String name, String sku, String category, Storage storage, Long quantity, String supplier, Tag tag) {
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.storage = storage;
        this.quantity = quantity;
        this.supplier = supplier;
        this.tag = tag;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getSku() {
        return this.sku;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public Storage getStorage() {
        return this.storage;
    }
    
    public Long getQuantity() {
        return this.quantity;
    }
    
    public String getSupplier() {
        return this.supplier;
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

    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
    //     if (!(o instanceof Product))
    //         return false;
    //     Product product = (Product) o;
    //     return Objects.equals(this.id, product.id) && Objects.equals(this.name, product.name)
    //                 && Objects.equals(this.sku, product.sku) && Objects.equals(this.category, product.category)
    //                 && Objects.equals(this.location, product.location) && Objects.equals(this.quantity, product.quantity)
    //                 && Objects.equals(this.supplier, product.supplier) && Objects.equals(this.tag, product.tag);
    // }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(this.id,
    //                 this.name,
    //                 this.sku,
    //                 this.category,
    //                 this.location,
    //                 this.quantity,
    //                 this.supplier,
    //                 this.tag);
    // }

    // @Override
    // public String toString() {
    //     return "Product{" + "id=" + this.id + ", name='" + this.name + '\'' + ", sku='" + this.sku + '\''
    //                 + ", category='" + this.category + '\'' + ", location='" + this.location + '\'' + ", quantity=" + this.quantity
    //                 + ", supplier='" + this.supplier + '\'' + "}";
    // }
}


