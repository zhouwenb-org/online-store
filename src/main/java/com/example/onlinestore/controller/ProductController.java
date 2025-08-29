package com.example.onlinestore.controller;

import com.example.onlinestore.annotation.RequireAdmin;
import com.example.onlinestore.annotation.ValidateParams;
import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.ErrorResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.dto.UpdateProductRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageSource messageSource;

    /**
     * 创建商品
     * 
     * @param request 创建商品请求
     * @return 创建的商品信息
     */
    @PostMapping
    @RequireAdmin
    @ValidateParams
    public ResponseEntity<?> createProduct(@RequestBody @Valid CreateProductRequest request) {
        try {
            logger.debug("开始创建商品，请求参数：{}", request);
            Product product = productService.createProduct(request);
            logger.debug("商品创建成功：{}", product.getName());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            logger.warn("创建商品失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("创建商品失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 获取商品列表
     * 
     * @param request 分页查询参数
     * @return 商品列表分页数据
     */
    @GetMapping
    @ValidateParams
    public ResponseEntity<?> listProducts(@Valid ProductPageRequest request) {
        try {
            logger.debug("开始查询商品列表，请求参数：{}", request);
            return ResponseEntity.ok(productService.listProducts(request));
        } catch (IllegalArgumentException e) {
            logger.warn("查询商品列表失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("查询商品列表失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 根据ID获取商品详情
     * 
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            logger.debug("开始根据ID查询商品：{}", id);
            Product product = productService.getProductById(id);
            logger.debug("查询到商品：{}", product.getName());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            logger.warn("查询商品失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("查询商品失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 获取所有商品分类
     * 
     * @return 商品分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            logger.debug("开始查询所有商品分类");
            return ResponseEntity.ok(productService.getAllCategories());
        } catch (Exception e) {
            logger.error("查询商品分类失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 更新商品
     * 
     * @param id 商品ID
     * @param request 更新商品请求
     * @return 更新后的商品信息
     */
    @PutMapping("/{id}")
    @RequireAdmin
    @ValidateParams
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) {
        try {
            logger.debug("开始更新商品，ID：{}，请求参数：{}", id, request);
            Product product = productService.updateProduct(id, request);
            logger.debug("商品更新成功：{}", product.getName());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            logger.warn("更新商品失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("更新商品失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 删除商品
     * 
     * @param id 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            logger.debug("开始删除商品，ID：{}", id);
            productService.deleteProductById(id);
            logger.debug("商品删除成功，ID：{}", id);
            
            String successMessage = messageSource.getMessage(
                "success.product.deleted", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (IllegalArgumentException e) {
            logger.warn("删除商品失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("删除商品失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }
} 