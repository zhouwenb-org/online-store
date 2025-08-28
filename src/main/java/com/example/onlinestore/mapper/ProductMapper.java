package com.example.onlinestore.mapper;

import com.example.onlinestore.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    void insertProduct(Product product);
    
    List<Product> findWithPagination(@Param("name") String name, 
                                    @Param("category") String category,
                                    @Param("minPrice") java.math.BigDecimal minPrice,
                                    @Param("maxPrice") java.math.BigDecimal maxPrice,
                                    @Param("offset") int offset, 
                                    @Param("limit") int limit);
    
    long countTotal(@Param("name") String name,
                   @Param("category") String category,
                   @Param("minPrice") java.math.BigDecimal minPrice,
                   @Param("maxPrice") java.math.BigDecimal maxPrice);

    List<Product> findAll();
    
    Product findById(@Param("id") Long id);
    
    List<String> findAllCategories();
    
    int deleteById(@Param("id") Long id);
} 