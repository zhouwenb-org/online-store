package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Test message");
    }

    @Nested
    class CreateProductTests {

        @Test
        void testCreateProduct_Success() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            Product createdProduct = new Product();
            createdProduct.setId(1L);
            createdProduct.setName("Test Product");
            createdProduct.setCategory("Electronics");
            createdProduct.setPrice(new BigDecimal("99.99"));
            createdProduct.setCreatedAt(LocalDateTime.now());
            createdProduct.setUpdatedAt(LocalDateTime.now());

            when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(createdProduct);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Test Product"))
                    .andExpect(jsonPath("$.category").value("Electronics"))
                    .andExpect(jsonPath("$.price").value(99.99));

            verify(productService).createProduct(any(CreateProductRequest.class));
        }

        @Test
        void testCreateProduct_InvalidInput() throws Exception {
            CreateProductRequest request = new CreateProductRequest();

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testCreateProduct_ServiceException() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new IllegalArgumentException("Invalid product data"));

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid product data"));

            verify(productService).createProduct(any(CreateProductRequest.class));
        }

        @Test
        void testCreateProduct_InternalServerError() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new RuntimeException("Database error"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                    .thenReturn("Internal server error");

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Internal server error"));

            verify(productService).createProduct(any(CreateProductRequest.class));
            verify(messageSource).getMessage(eq("error.system.internal"), isNull(), any(Locale.class));
        }
    }

    @Nested
    class ListProductsTests {

        @Test
        void testListProducts_Success() throws Exception {
            List<Product> products = createMockProducts(5);
            PageResponse<Product> pageResponse = new PageResponse<>();
            pageResponse.setRecords(products);
            pageResponse.setTotal(5L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records.length()").value(5))
                    .andExpect(jsonPath("$.total").value(5))
                    .andExpect(jsonPath("$.pageNum").value(1))
                    .andExpect(jsonPath("$.pageSize").value(10));

            verify(productService).listProducts(any(ProductPageRequest.class));
        }

        @Test
        void testListProducts_WithNameFilter() throws Exception {
            List<Product> products = createMockProducts(1);
            PageResponse<Product> pageResponse = new PageResponse<>();
            pageResponse.setRecords(products);
            pageResponse.setTotal(1L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("name", "Product 1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.total").value(1));

            verify(productService).listProducts(any(ProductPageRequest.class));
        }

        @Test
        void testListProducts_InvalidPageSize() throws Exception {
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testListProducts_InvalidPageNum() throws Exception {
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "0")
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testListProducts_ServiceException() throws Exception {
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new IllegalArgumentException("Invalid parameters"));

            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid parameters"));

            verify(productService).listProducts(any(ProductPageRequest.class));
        }

        @Test
        void testListProducts_InternalServerError() throws Exception {
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new RuntimeException("Database error"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                    .thenReturn("Internal server error");

            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Internal server error"));

            verify(productService).listProducts(any(ProductPageRequest.class));
            verify(messageSource).getMessage(eq("error.system.internal"), isNull(), any(Locale.class));
        }

        private List<Product> createMockProducts(int count) {
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                Product product = new Product();
                product.setId((long) i);
                product.setName("Product " + i);
                product.setCategory("Category " + i);
                product.setPrice(new BigDecimal("10.00").multiply(new BigDecimal(i)));
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                products.add(product);
            }
            return products;
        }
    }
}
