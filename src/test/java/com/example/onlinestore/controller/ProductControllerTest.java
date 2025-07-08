package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.ErrorResponse;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_Success() throws Exception {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("测试商品");
        request.setCategory("电子产品");
        request.setPrice(new BigDecimal("99.99"));

        Product expectedProduct = new Product();
        expectedProduct.setId(1L);
        expectedProduct.setName("测试商品");
        expectedProduct.setCategory("电子产品");
        expectedProduct.setPrice(new BigDecimal("99.99"));
        expectedProduct.setCreatedAt(LocalDateTime.now());
        expectedProduct.setUpdatedAt(LocalDateTime.now());

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(expectedProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("测试商品"))
                .andExpect(jsonPath("$.category").value("电子产品"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(productService, times(1)).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_IllegalArgumentException() throws Exception {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("测试商品");
        request.setCategory("电子产品");
        request.setPrice(new BigDecimal("99.99"));

        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new IllegalArgumentException("商品名称已存在"));

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("商品名称已存在"));

        verify(productService, times(1)).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_InternalServerError() throws Exception {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("测试商品");
        request.setCategory("电子产品");
        request.setPrice(new BigDecimal("99.99"));

        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new RuntimeException("数据库连接失败"));
        when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn("系统内部错误");

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("系统内部错误"));

        verify(productService, times(1)).createProduct(any(CreateProductRequest.class));
        verify(messageSource, times(1)).getMessage(eq("error.system.internal"), isNull(), any(Locale.class));
    }

    @Test
    void listProducts_Success() throws Exception {
        // Given
        ProductPageRequest request = new ProductPageRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setName("测试");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("测试商品1");
        product1.setCategory("电子产品");
        product1.setPrice(new BigDecimal("99.99"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("测试商品2");
        product2.setCategory("服装");
        product2.setPrice(new BigDecimal("49.99"));

        PageResponse<Product> pageResponse = new PageResponse<>();
        pageResponse.setRecords(Arrays.asList(product1, product2));
        pageResponse.setTotal(2L);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);

        when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("name", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records", hasSize(2)))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));

        verify(productService, times(1)).listProducts(any(ProductPageRequest.class));
    }

    @Test
    void listProducts_IllegalArgumentException() throws Exception {
        // Given
        when(productService.listProducts(any(ProductPageRequest.class)))
                .thenThrow(new IllegalArgumentException("页码不能小于1"));

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("pageNum", "0")
                .param("pageSize", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("页码不能小于1"));

        verify(productService, times(1)).listProducts(any(ProductPageRequest.class));
    }

    @Test
    void listProducts_InternalServerError() throws Exception {
        // Given
        when(productService.listProducts(any(ProductPageRequest.class)))
                .thenThrow(new RuntimeException("数据库查询失败"));
        when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn("系统内部错误");

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("系统内部错误"));

        verify(productService, times(1)).listProducts(any(ProductPageRequest.class));
        verify(messageSource, times(1)).getMessage(eq("error.system.internal"), isNull(), any(Locale.class));
    }

    @Test
    void listProducts_WithoutNameParameter() throws Exception {
        // Given
        PageResponse<Product> pageResponse = new PageResponse<>();
        pageResponse.setRecords(Arrays.asList());
        pageResponse.setTotal(0L);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);

        when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));

        verify(productService, times(1)).listProducts(any(ProductPageRequest.class));
    }
}