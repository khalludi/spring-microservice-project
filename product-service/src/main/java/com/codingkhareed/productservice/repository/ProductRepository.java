package com.codingkhareed.productservice.repository;

import com.codingkhareed.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

}
