package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("商品控制器测试")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private ProductService productService;

    private CreateProductRequest createRequest;
    private Product product;
    private ProductPageRequest pageRequest;
    private PageResponse<Product> pageResponse;

    @BeforeEach
    void setUp() {
        // 准备创建商品请求数据
        createRequest = new CreateProductRequest();
        createRequest.setName("iPhone 15");
        createRequest.setCategory("手机");
        createRequest.setPrice(new BigDecimal("5999.00"));

        // 准备商品数据
        product = new Product();
        product.setId(1L);
        product.setName("iPhone 15");
        product.setCategory("手机");
        product.setPrice(new BigDecimal("5999.00"));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // 准备分页请求数据
        pageRequest = new ProductPageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        // 准备分页响应数据
        pageResponse = new PageResponse<>();
        pageResponse.setRecords(Arrays.asList(product));
        pageResponse.setTotal(1L);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);
    }

    @Nested
    @DisplayName("创建商品接口测试")
    class CreateProductTests {

        @Test
        @DisplayName("创建商品成功")
        void whenCreateProductSucceeds_thenReturnProduct() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(product);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(product.getId()))
                    .andExpect(jsonPath("$.name").value(product.getName()))
                    .andExpect(jsonPath("$.category").value(product.getCategory()))
                    .andExpect(jsonPath("$.price").value(product.getPrice()));
        }

        @Test
        @DisplayName("创建商品失败 - 参数校验错误")
        void whenCreateProductWithInvalidParams_thenReturnBadRequest() throws Exception {
            // 准备无效数据
            createRequest.setName(""); // 空名称
            createRequest.setPrice(new BigDecimal("-1")); // 负价格

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("创建商品失败 - 业务逻辑异常")
        void whenCreateProductWithBusinessException_thenReturnBadRequest() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new IllegalArgumentException("商品名称已存在"));

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("商品名称已存在"));
        }

        @Test
        @DisplayName("创建商品失败 - 系统错误（英文）")
        void whenCreateProductWithSystemErrorInEnglish_thenReturnInternalServerError() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new RuntimeException("Database error"));

            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.ENGLISH);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "en")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        @DisplayName("创建商品失败 - 系统错误（中文）")
        void whenCreateProductWithSystemErrorInChinese_thenReturnInternalServerError() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new RuntimeException("数据库错误"));

            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.SIMPLIFIED_CHINESE);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "zh-CN")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }
    }

    @Nested
    @DisplayName("查询商品列表接口测试")
    class ListProductsTests {

        @Test
        @DisplayName("查询商品列表成功")
        void whenListProductsSucceeds_thenReturnPageResponse() throws Exception {
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records[0].id").value(product.getId()))
                    .andExpect(jsonPath("$.records[0].name").value(product.getName()))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.pageNum").value(1))
                    .andExpect(jsonPath("$.pageSize").value(10));
        }

        @Test
        @DisplayName("查询商品列表成功 - 带商品名称筛选")
        void whenListProductsWithNameFilter_thenReturnFilteredResults() throws Exception {
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("name", "iPhone"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.total").value(1));
        }

        @Test
        @DisplayName("查询商品列表失败 - 参数校验错误")
        void whenListProductsWithInvalidParams_thenReturnBadRequest() throws Exception {
            // 执行测试 - 无效的页码
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "0") // 无效页码
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest());

            // 执行测试 - 无效的页面大小
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "101")) // 超出最大限制
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("查询商品列表失败 - 业务逻辑异常")
        void whenListProductsWithBusinessException_thenReturnBadRequest() throws Exception {
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new IllegalArgumentException("查询参数无效"));

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("查询参数无效"));
        }

        @Test
        @DisplayName("查询商品列表失败 - 系统错误")
        void whenListProductsWithSystemError_thenReturnInternalServerError() throws Exception {
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.SIMPLIFIED_CHINESE);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .header("Accept-Language", "zh-CN"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }
    }
}