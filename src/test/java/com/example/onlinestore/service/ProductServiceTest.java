package com.example.onlinestore.service;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("商品服务测试")
public class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private CreateProductRequest createRequest;
    private Product product;
    private ProductPageRequest pageRequest;

    @BeforeEach
    void setUp() {
        // 准备创建商品请求数据
        createRequest = new CreateProductRequest();
        createRequest.setName("测试商品");
        createRequest.setCategory("电子产品");
        createRequest.setPrice(new BigDecimal("199.99"));

        // 准备商品数据
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setCategory("电子产品");
        product.setPrice(new BigDecimal("199.99"));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // 准备分页请求数据
        pageRequest = new ProductPageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        // 清空缓存
        ReflectionTestUtils.setField(productService, "producteCache", new java.util.HashMap<>());
    }

    @Nested
    @DisplayName("创建商品测试")
    class CreateProductTests {

        @Test
        @DisplayName("成功创建商品")
        void whenCreateProduct_thenReturnCreatedProduct() {
            // 设置mock行为
            doAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                p.setId(1L);
                return null;
            }).when(productMapper).insertProduct(any(Product.class));

            // 执行测试
            Product result = productService.createProduct(createRequest);

            // 验证结果
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("测试商品", result.getName());
            assertEquals("电子产品", result.getCategory());
            assertEquals(new BigDecimal("199.99"), result.getPrice());
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());

            // 验证调用
            verify(productMapper).insertProduct(any(Product.class));

            // 验证产品已加入缓存
            Object cache = ReflectionTestUtils.getField(productService, "producteCache");
            assertNotNull(cache);
        }

        @Test
        @DisplayName("创建商品时缓存容量超限")
        void whenCreateProductWithFullCache_thenRemoveOldestItem() {
            // 准备满缓存的情况
            java.util.Map<Long, Product> fullCache = new java.util.HashMap<>();
            for (long i = 1; i <= 1000; i++) {
                Product p = new Product();
                p.setId(i);
                p.setName("商品" + i);
                fullCache.put(i, p);
            }
            ReflectionTestUtils.setField(productService, "producteCache", fullCache);

            // 设置mock行为
            doAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                p.setId(1001L);
                return null;
            }).when(productMapper).insertProduct(any(Product.class));

            // 执行测试
            Product result = productService.createProduct(createRequest);

            // 验证结果
            assertNotNull(result);
            assertEquals(1001L, result.getId());

            // 验证缓存大小没有超出限制
            @SuppressWarnings("unchecked")
            java.util.Map<Long, Product> cache = (java.util.Map<Long, Product>) 
                ReflectionTestUtils.getField(productService, "producteCache");
            assertTrue(cache.size() <= 1000);
            assertTrue(cache.containsKey(1001L));
        }

        @Test
        @DisplayName("创建商品时数据库操作失败")
        void whenCreateProductThrowsException_thenPropagateException() {
            // 设置mock行为
            doThrow(new RuntimeException("数据库连接失败")).when(productMapper).insertProduct(any(Product.class));

            // 执行测试并验证异常
            assertThrows(RuntimeException.class, () -> productService.createProduct(createRequest));

            // 验证调用
            verify(productMapper).insertProduct(any(Product.class));
        }
    }

    @Nested
    @DisplayName("查询商品列表测试")
    class ListProductsTests {

        @Test
        @DisplayName("缓存为空时从数据库查询")
        void whenCacheIsEmpty_thenLoadFromDatabase() {
            // 准备测试数据
            List<Product> products = createProductList(5);
            when(productMapper.findAll()).thenReturn(products);
            when(productMapper.findWithPagination(anyString(), anyInt(), anyInt())).thenReturn(products.subList(0, 2));
            when(productMapper.countTotal(anyString())).thenReturn(5L);

            // 执行测试
            PageResponse<Product> result = productService.listProducts(pageRequest);

            // 验证结果
            assertNotNull(result);
            assertEquals(2, result.getRecords().size());
            assertEquals(5L, result.getTotal());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());

            // 验证调用
            verify(productMapper).findAll();
            verify(productMapper).findWithPagination(null, 0, 10);
            verify(productMapper).countTotal(null);
        }

        @Test
        @DisplayName("使用缓存进行分页查询")
        void whenCacheHasData_thenUseCache() {
            // 准备缓存数据
            java.util.Map<Long, Product> cache = new java.util.HashMap<>();
            List<Product> products = createProductList(5);
            for (Product p : products) {
                cache.put(p.getId(), p);
            }
            ReflectionTestUtils.setField(productService, "producteCache", cache);

            // 执行测试
            PageResponse<Product> result = productService.listProducts(pageRequest);

            // 验证结果
            assertNotNull(result);
            assertEquals(5, result.getTotal());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());

            // 验证没有调用数据库
            verify(productMapper, never()).findAll();
            verify(productMapper, never()).findWithPagination(anyString(), anyInt(), anyInt());
            verify(productMapper, never()).countTotal(anyString());
        }

        @Test
        @DisplayName("根据商品名称精确查询缓存")
        void whenSearchByName_thenReturnMatchedProduct() {
            // 准备缓存数据
            java.util.Map<Long, Product> cache = new java.util.HashMap<>();
            Product targetProduct = new Product();
            targetProduct.setId(1L);
            targetProduct.setName("特定商品");
            cache.put(1L, targetProduct);
            ReflectionTestUtils.setField(productService, "producteCache", cache);

            // 设置查询名称
            pageRequest.setName("特定商品");

            // 执行测试
            PageResponse<Product> result = productService.listProducts(pageRequest);

            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertEquals("特定商品", result.getRecords().get(0).getName());
        }

        @Test
        @DisplayName("缓存超过限制时使用数据库查询")
        void whenCacheExceedsLimit_thenUseDatabase() {
            // 准备数据
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);
            
            List<Product> mockProducts = Arrays.asList(product);
            
            // 设置mock行为 - 使用正确的参数
            when(productMapper.findWithPagination(isNull(), eq(0), eq(10))).thenReturn(mockProducts);
            when(productMapper.countTotal(isNull())).thenReturn(5L);
            
            // 执行测试
            PageResponse<Product> result = productService.listProducts(request);
            
            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertEquals(5, result.getTotal());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());
        }

        @Test
        @DisplayName("查询第二页数据正确计算偏移量")
        void whenQuerySecondPage_thenReturnCorrectOffset() {
            // 准备数据
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(2);
            request.setPageSize(3);
            
            List<Product> mockProducts = Arrays.asList(product);
            
            // 设置mock行为
            when(productMapper.findWithPagination(isNull(), eq(3), eq(3))).thenReturn(mockProducts);
            when(productMapper.countTotal(isNull())).thenReturn(10L);
            
            // 执行测试
            PageResponse<Product> result = productService.listProducts(request);
            
            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertEquals(2, result.getPageNum());
            assertEquals(3, result.getPageSize());
            assertEquals(10L, result.getTotal());
            
            // 验证调用了正确的偏移量
            verify(productMapper).findWithPagination(null, 3, 3);
        }

        @Test
        @DisplayName("查询时数据库操作失败")
        void whenDatabaseQueryFails_thenPropagateException() {
            // 设置mock行为
            when(productMapper.findAll()).thenThrow(new RuntimeException("数据库连接失败"));

            // 执行测试并验证异常
            assertThrows(RuntimeException.class, () -> productService.listProducts(pageRequest));
        }
    }

    /**
     * 创建测试用的商品列表
     */
    private List<Product> createProductList(int size) {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Product p = new Product();
            p.setId((long) i);
            p.setName("商品" + i);
            p.setCategory("分类" + i);
            p.setPrice(new BigDecimal("100.00").add(new BigDecimal(i)));
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            products.add(p);
        }
        return products;
    }
}