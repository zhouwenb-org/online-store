package com.example.onlinestore.service;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductRequest request);
    PageResponse<Product> listProducts(ProductPageRequest request);
    Product getProductById(Long id);
    List<String> getAllCategories();
} 