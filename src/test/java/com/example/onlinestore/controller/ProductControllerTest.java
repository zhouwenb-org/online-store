package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private MessageSource messageSource;

    private ProductController productController;
    private ObjectMapper objectMapper;

    private Product testProduct;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        // 创建controller实例并注入mock依赖
        productController = new ProductController();
        ReflectionTestUtils.setField(productController, "productService", productService);
        ReflectionTestUtils.setField(productController, "messageSource", messageSource);
        
        // 验证注入成功
        Object injectedProductService = ReflectionTestUtils.getField(productController, "productService");
        Object injectedMessageSource = ReflectionTestUtils.getField(productController, "messageSource");
        assertNotNull(injectedProductService, "ProductService should be injected");
        assertNotNull(injectedMessageSource, "MessageSource should be injected");
        
        // 初始化MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        
        // 准备测试数据
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setCategory("电子产品");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateProductRequest();
        createRequest.setName("测试商品");
        createRequest.setCategory("电子产品");
        createRequest.setPrice(new BigDecimal("999.99"));
    }

    @Test
    public void testCreateProduct_Success() throws Exception {
        // Given
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("测试商品"))
                .andExpect(jsonPath("$.category").value("电子产品"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    public void testCreateProduct_IllegalArgumentException() throws Exception {
        // Given
        String errorMessage = "商品名称已存在";
        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void testCreateProduct_InternalServerError() throws Exception {
        // Given
        String internalErrorMessage = "系统内部错误";
        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));
        when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn(internalErrorMessage);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(internalErrorMessage));
    }

    @Test
    public void testListProducts_Success() throws Exception {
        // Given
        PageResponse<Product> pageResponse = new PageResponse<>();
        pageResponse.setRecords(Arrays.asList(testProduct));
        pageResponse.setTotal(1L);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);

        when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records[0].id").value(1))
                .andExpect(jsonPath("$.records[0].name").value("测试商品"))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
    }
}