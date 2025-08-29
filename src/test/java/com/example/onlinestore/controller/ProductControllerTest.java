package com.example.onlinestore.controller;

import com.example.onlinestore.dto.*;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setCategory("电子产品");
        testProduct.setPrice(new BigDecimal("299.99"));
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateProductRequest();
        createRequest.setName("新商品");
        createRequest.setCategory("家居用品");
        createRequest.setPrice(new BigDecimal("199.99"));

        updateRequest = new UpdateProductRequest();
        updateRequest.setName("更新商品");
        updateRequest.setPrice(new BigDecimal("399.99"));
    }

    @Nested
    class CreateProductTests {

        @Test
        void createProduct_Success() throws Exception {
            // Given
            when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(testProduct);

            // When & Then
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("测试商品"))
                .andExpect(jsonPath("$.category").value("电子产品"))
                .andExpect(jsonPath("$.price").value(299.99));

            verify(productService).createProduct(any(CreateProductRequest.class));
        }

        @Test
        void createProduct_ValidationError() throws Exception {
            // Given
            CreateProductRequest invalidRequest = new CreateProductRequest();
            // Missing required fields

            // When & Then
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }

        @Test
        void createProduct_ServiceError() throws Exception {
            // Given
            when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new IllegalArgumentException("商品名称已存在"));

            // When & Then
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("商品名称已存在"));
        }

        @Test
        void createProduct_SystemError() throws Exception {
            // Given
            when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn("系统内部错误");

            // When & Then
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }

    @Nested
    class ListProductsTests {

        @Test
        void listProducts_Success() throws Exception {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            PageResponse<Product> pageResponse = new PageResponse<>();
            pageResponse.setRecords(products);
            pageResponse.setTotal(1L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(productService.listProducts(any(ProductPageRequest.class)))
                .thenReturn(pageResponse);

            // When & Then
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
        }

        @Test
        void listProducts_WithFilters() throws Exception {
            // Given
            PageResponse<Product> pageResponse = new PageResponse<>();
            pageResponse.setRecords(Arrays.asList());
            pageResponse.setTotal(0L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(productService.listProducts(any(ProductPageRequest.class)))
                .thenReturn(pageResponse);

            // When & Then
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("name", "测试商品")
                    .param("category", "电子产品")
                    .param("minPrice", "100.00")
                    .param("maxPrice", "500.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.total").value(0));
        }

        @Test
        void listProducts_InvalidPageNumber() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "0") // Invalid page number
                    .param("pageSize", "10"))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetProductByIdTests {

        @Test
        void getProductById_Success() throws Exception {
            // Given
            when(productService.getProductById(1L)).thenReturn(testProduct);

            // When & Then
            mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("测试商品"))
                .andExpect(jsonPath("$.category").value("电子产品"))
                .andExpect(jsonPath("$.price").value(299.99));
        }

        @Test
        void getProductById_NotFound() throws Exception {
            // Given
            when(productService.getProductById(999L))
                .thenThrow(new IllegalArgumentException("商品不存在，ID：999"));

            // When & Then
            mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("商品不存在，ID：999"));
        }
    }

    @Nested
    class UpdateProductTests {

        @Test
        void updateProduct_Success() throws Exception {
            // Given
            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setName("更新商品");
            updatedProduct.setCategory("电子产品");
            updatedProduct.setPrice(new BigDecimal("399.99"));

            when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class)))
                .thenReturn(updatedProduct);

            // When & Then
            mockMvc.perform(put("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("更新商品"))
                .andExpect(jsonPath("$.price").value(399.99));
        }

        @Test
        void updateProduct_NotFound() throws Exception {
            // Given
            when(productService.updateProduct(eq(999L), any(UpdateProductRequest.class)))
                .thenThrow(new IllegalArgumentException("商品不存在，ID：999"));

            // When & Then
            mockMvc.perform(put("/api/products/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("商品不存在，ID：999"));
        }

        @Test
        void updateProduct_ValidationError() throws Exception {
            // Given
            UpdateProductRequest invalidRequest = new UpdateProductRequest();
            invalidRequest.setPrice(new BigDecimal("-1")); // Invalid price

            // When & Then
            mockMvc.perform(put("/api/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeleteProductTests {

        @Test
        void deleteProduct_Success() throws Exception {
            // Given
            doNothing().when(productService).deleteProductById(1L);
            when(messageSource.getMessage(eq("success.product.deleted"), isNull(), any(Locale.class)))
                .thenReturn("商品删除成功");

            // When & Then
            mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("商品删除成功"));

            verify(productService).deleteProductById(1L);
        }

        @Test
        void deleteProduct_NotFound() throws Exception {
            // Given
            doThrow(new IllegalArgumentException("商品不存在，ID：999"))
                .when(productService).deleteProductById(999L);

            // When & Then
            mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("商品不存在，ID：999"));
        }

        @Test
        void deleteProduct_SystemError() throws Exception {
            // Given
            doThrow(new RuntimeException("Database error"))
                .when(productService).deleteProductById(1L);
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn("系统内部错误");

            // When & Then
            mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }

    @Nested
    class GetAllCategoriesTests {

        @Test
        void getAllCategories_Success() throws Exception {
            // Given
            List<String> categories = Arrays.asList("电子产品", "家居用品", "服装");
            when(productService.getAllCategories()).thenReturn(categories);

            // When & Then
            mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("电子产品"))
                .andExpect(jsonPath("$[1]").value("家居用品"))
                .andExpect(jsonPath("$[2]").value("服装"));
        }

        @Test
        void getAllCategories_Empty() throws Exception {
            // Given
            when(productService.getAllCategories()).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void getAllCategories_SystemError() throws Exception {
            // Given
            when(productService.getAllCategories())
                .thenThrow(new RuntimeException("Database connection failed"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn("系统内部错误");

            // When & Then
            mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }
}