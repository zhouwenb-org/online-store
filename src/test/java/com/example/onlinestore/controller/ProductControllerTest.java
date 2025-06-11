package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
@DisplayName("商品控制器测试")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private MessageSource messageSource;

    private Product product;
    private CreateProductRequest createRequest;
    private PageResponse<Product> pageResponse;

    @BeforeEach
    void setUp() {
        // 准备创建商品请求数据
        createRequest = new CreateProductRequest();
        createRequest.setName("测试商品");
        createRequest.setCategory("电子产品");
        createRequest.setPrice(new BigDecimal("199.99"));

        // 准备商品对象
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setCategory("电子产品");
        product.setPrice(new BigDecimal("199.99"));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // 准备分页响应数据
        List<Product> products = Arrays.asList(product);
        pageResponse = new PageResponse<>();
        pageResponse.setRecords(products);
        pageResponse.setTotal(1);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);
    }

    @AfterEach
    void tearDown() {
        // 清理工作（如需要）
    }

    @Nested
    @DisplayName("创建商品接口测试")
    class CreateProductTests {
        
        @Test
        @DisplayName("成功创建商品")
        void whenCreateProductSucceeds_thenReturnCreatedProduct() throws Exception {
            // 设置mock行为
            when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(product);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("测试商品"))
                    .andExpect(jsonPath("$.category").value("电子产品"))
                    .andExpect(jsonPath("$.price").value(199.99));
        }

        @Test
        @DisplayName("创建商品失败 - 商品名称为空")
        void whenCreateProductWithEmptyName_thenReturnBadRequest() throws Exception {
            createRequest.setName("");

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("创建商品失败 - 商品分类为空")
        void whenCreateProductWithEmptyCategory_thenReturnBadRequest() throws Exception {
            createRequest.setCategory("");

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("创建商品失败 - 价格为空")
        void whenCreateProductWithNullPrice_thenReturnBadRequest() throws Exception {
            createRequest.setPrice(null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("创建商品失败 - 价格太低")
        void whenCreateProductWithLowPrice_thenReturnBadRequest() throws Exception {
            createRequest.setPrice(new BigDecimal("0.00"));

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("创建商品失败 - 业务异常")
        void whenCreateProductThrowsBusinessException_thenReturnBadRequest() throws Exception {
            // 设置mock行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new IllegalArgumentException("商品名称已存在"));

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("商品名称已存在"));
        }

        @Test
        @DisplayName("创建商品失败 - 系统异常")
        void whenCreateProductThrowsSystemException_thenReturnInternalError() throws Exception {
            // 设置mock行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new RuntimeException("数据库连接失败"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                    .thenReturn("系统内部错误");

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Token", "admin-token")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }

    @Nested
    @DisplayName("获取商品列表接口测试")
    class ListProductsTests {
        
        @Test
        @DisplayName("成功获取商品列表")
        void whenListProductsSucceeds_thenReturnProductList() throws Exception {
            // 设置mock行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records.length()").value(1))
                    .andExpect(jsonPath("$.records[0].id").value(1))
                    .andExpect(jsonPath("$.records[0].name").value("测试商品"))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.pageNum").value(1))
                    .andExpect(jsonPath("$.pageSize").value(10));
        }

        @Test
        @DisplayName("根据商品名称查询")
        void whenListProductsWithName_thenReturnFilteredProducts() throws Exception {
            // 设置mock行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("name", "测试商品")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.total").value(1));
        }

        @Test
        @DisplayName("获取商品列表失败 - 页码无效")
        void whenListProductsWithInvalidPageNum_thenReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("获取商品列表失败 - 页大小超过限制")
        void whenListProductsWithLargePageSize_thenReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "101")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("获取商品列表失败 - 业务异常")
        void whenListProductsThrowsBusinessException_thenReturnBadRequest() throws Exception {
            // 设置mock行为
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new IllegalArgumentException("查询参数无效"));

            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("查询参数无效"));
        }

        @Test
        @DisplayName("获取商品列表失败 - 系统异常")
        void whenListProductsThrowsSystemException_thenReturnInternalError() throws Exception {
            // 设置mock行为
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new RuntimeException("数据库连接失败"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                    .thenReturn("系统内部错误");

            mockMvc.perform(get("/api/products")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }
}