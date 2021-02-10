package com.sl.ms.inventorymanagement.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.sl.ms.inventorymanagement.model.Product;

@RestResource(path="products", rel="products")
public interface ProductRepository extends CrudRepository<Product, Long>{

}
