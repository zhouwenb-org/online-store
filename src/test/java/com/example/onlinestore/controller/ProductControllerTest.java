package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.model.User;
import com.example.onlinestore.service.ProductService;
import com.example.onlinestore.service.UserService;
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
import java.util.Collections;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockBean
    private UserService userService;

    private CreateProductRequest createRequest;
    private Product mockProduct;
    private ProductPageRequest pageRequest;
    private PageResponse<Product> pageResponse;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // 准备创建商品请求数据
        createRequest = new CreateProductRequest();
        createRequest.setName("测试商品");
        createRequest.setCategory("测试分类");
        createRequest.setPrice(new BigDecimal("99.99"));

        // 准备模拟商品数据
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("测试商品");
        mockProduct.setCategory("测试分类");
        mockProduct.setPrice(new BigDecimal("99.99"));
        mockProduct.setCreatedAt(LocalDateTime.now());
        mockProduct.setUpdatedAt(LocalDateTime.now());

        // 准备分页查询请求数据
        pageRequest = new ProductPageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        // 准备分页响应数据
        pageResponse = new PageResponse<>();
        pageResponse.setRecords(Arrays.asList(mockProduct));
        pageResponse.setTotal(1L);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);

        // 准备用户数据 - 使用管理员用户
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("admin"); // 设置为管理员用户名

        // 设置认证mock
        when(userService.getUserByToken("valid-token")).thenReturn(mockUser);
    }

    /**
     * 添加认证头的辅助方法
     */
    private static final String AUTH_TOKEN = "valid-token";

    @Nested
    @DisplayName("创建商品接口测试")
    class CreateProductTests {

        @Test
        @DisplayName("创建商品成功")
        void whenCreateProductSucceeds_thenReturnProduct() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(mockProduct);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mockProduct.getId()))
                    .andExpect(jsonPath("$.name").value(mockProduct.getName()))
                    .andExpect(jsonPath("$.category").value(mockProduct.getCategory()))
                    .andExpect(jsonPath("$.price").value(mockProduct.getPrice()))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @Test
        @DisplayName("创建商品失败 - 参数验证失败")
        void whenCreateProductWithInvalidParams_thenReturnBadRequest() throws Exception {
            // 准备无效请求数据
            CreateProductRequest invalidRequest = new CreateProductRequest();
            invalidRequest.setName(""); // 空名称
            invalidRequest.setCategory(""); // 空分类
            invalidRequest.setPrice(new BigDecimal("-1")); // 负价格

            String errorMessage = "商品参数不合法";

            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new IllegalArgumentException(errorMessage));

            // 执行测试 - Bean Validation会在进入controller之前拦截
            mockMvc.perform(post("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
            // 注意：Bean Validation错误的响应格式可能与自定义错误不同，所以不检查具体的message字段
        }

        @Test
        @DisplayName("创建商品失败 - 系统内部错误")
        void whenCreateProductSystemError_thenReturnInternalServerError() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.SIMPLIFIED_CHINESE);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "zh-CN")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        @DisplayName("创建商品失败 - 多语言错误消息")
        void whenCreateProductSystemError_thenReturnLocalizedErrorMessage() throws Exception {
            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // 测试英文系统错误
            String enErrorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.ENGLISH);
            mockMvc.perform(post("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "en")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(enErrorMessage));

            // 测试中文系统错误
            String zhErrorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.SIMPLIFIED_CHINESE);
            mockMvc.perform(post("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "zh-CN")
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(zhErrorMessage));
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
                    .header("X-Token", AUTH_TOKEN)
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records[0].id").value(mockProduct.getId()))
                    .andExpect(jsonPath("$.records[0].name").value(mockProduct.getName()))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.pageNum").value(1))
                    .andExpect(jsonPath("$.pageSize").value(10));
        }

        @Test
        @DisplayName("查询商品列表成功 - 带商品名称过滤")
        void whenListProductsWithNameFilter_thenReturnFilteredResults() throws Exception {
            // 设置分页请求包含商品名称过滤
            pageRequest.setName("测试商品");
            
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("name", "测试商品"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records[0].name").value("测试商品"))
                    .andExpect(jsonPath("$.total").value(1));
        }

        @Test
        @DisplayName("查询商品列表成功 - 空结果")
        void whenListProductsReturnsEmpty_thenReturnEmptyPageResponse() throws Exception {
            // 准备空分页响应
            PageResponse<Product> emptyResponse = new PageResponse<>();
            emptyResponse.setRecords(Collections.emptyList());
            emptyResponse.setTotal(0L);
            emptyResponse.setPageNum(1);
            emptyResponse.setPageSize(10);

            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(emptyResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records").isEmpty())
                    .andExpect(jsonPath("$.total").value(0));
        }

        @Test
        @DisplayName("查询商品列表失败 - 参数验证失败")
        void whenListProductsWithInvalidParams_thenReturnBadRequest() throws Exception {
            // 执行测试（页码为0，无效）- 这会触发Bean Validation
            mockMvc.perform(get("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .param("pageNum", "0")
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest());
            // 注意：Bean Validation错误的响应格式可能与自定义错误不同，所以不检查具体的message字段
        }

        @Test
        @DisplayName("查询商品列表失败 - 系统内部错误")
        void whenListProductsSystemError_thenReturnInternalServerError() throws Exception {
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class)))
                    .thenThrow(new RuntimeException("Database query failed"));

            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.SIMPLIFIED_CHINESE);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .header("Accept-Language", "zh-CN"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        @DisplayName("查询商品列表 - 大分页测试")
        void whenListProductsWithLargePage_thenReturnResults() throws Exception {
            // 准备大分页数据
            pageRequest.setPageNum(100);
            pageRequest.setPageSize(50);
            
            PageResponse<Product> largePageResponse = new PageResponse<>();
            largePageResponse.setRecords(Collections.emptyList());
            largePageResponse.setTotal(0L);
            largePageResponse.setPageNum(100);
            largePageResponse.setPageSize(50);

            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(largePageResponse);

            // 执行测试
            mockMvc.perform(get("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .param("pageNum", "100")
                    .param("pageSize", "50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNum").value(100))
                    .andExpect(jsonPath("$.pageSize").value(50))
                    .andExpect(jsonPath("$.total").value(0));
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("创建商品 - 极长商品名称")
        void whenCreateProductWithLongName_thenHandleCorrectly() throws Exception {
            // 准备极长名称的商品
            CreateProductRequest longNameRequest = new CreateProductRequest();
            longNameRequest.setName("A".repeat(255)); // 255个字符的名称
            longNameRequest.setCategory("测试分类");
            longNameRequest.setPrice(new BigDecimal("99.99"));

            Product longNameProduct = new Product();
            longNameProduct.setId(1L);
            longNameProduct.setName(longNameRequest.getName());
            longNameProduct.setCategory(longNameRequest.getCategory());
            longNameProduct.setPrice(longNameRequest.getPrice());
            longNameProduct.setCreatedAt(LocalDateTime.now());
            longNameProduct.setUpdatedAt(LocalDateTime.now());

            // 设置 mock 行为
            when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(longNameProduct);

            // 执行测试
            mockMvc.perform(post("/api/products")
                    .header("X-Token", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(longNameRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(longNameRequest.getName()));
        }

        @Test
        @DisplayName("查询商品列表 - 最小分页参数")
        void whenListProductsWithMinimalParams_thenReturnResults() throws Exception {
            // 设置 mock 行为
            when(productService.listProducts(any(ProductPageRequest.class))).thenReturn(pageResponse);

            // 执行测试（不传分页参数，使用默认值）
            mockMvc.perform(get("/api/products")
                    .header("X-Token", AUTH_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNum").exists())
                    .andExpect(jsonPath("$.pageSize").exists());
        }
    }
}