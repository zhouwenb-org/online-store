package com.example.onlinestore.service.impl;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        // 设置缓存大小为默认值1000
        ReflectionTestUtils.setField(productService, "productCacheMaxSize", 1000);
    }

    @Nested
    class CreateProductTests {

        @Test
        void testCreateProduct_Success() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            Product savedProduct = new Product();
            savedProduct.setId(1L);
            savedProduct.setName("Test Product");
            savedProduct.setCategory("Electronics");
            savedProduct.setPrice(new BigDecimal("99.99"));
            savedProduct.setCreatedAt(LocalDateTime.now());
            savedProduct.setUpdatedAt(LocalDateTime.now());

            doAnswer(invocation -> {
                Product product = invocation.getArgument(0);
                product.setId(1L);
                return null;
            }).when(productMapper).insertProduct(any(Product.class));

            Product result = productService.createProduct(request);

            assertNotNull(result);
            assertEquals("Test Product", result.getName());
            assertEquals("Electronics", result.getCategory());
            assertEquals(new BigDecimal("99.99"), result.getPrice());
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());

            verify(productMapper).insertProduct(any(Product.class));
        }

        @Test
        void testCreateProduct_CacheManagement() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            doAnswer(invocation -> {
                Product product = invocation.getArgument(0);
                product.setId(1L);
                return null;
            }).when(productMapper).insertProduct(any(Product.class));

            Product result = productService.createProduct(request);

            assertNotNull(result);
            verify(productMapper).insertProduct(any(Product.class));
        }

        @Test
        void testCreateProduct_CacheSizeLimit() {
            // 设置较小的缓存大小用于测试
            ReflectionTestUtils.setField(productService, "productCacheMaxSize", 2);

            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            doAnswer(invocation -> {
                Product product = invocation.getArgument(0);
                product.setId(System.currentTimeMillis()); // 使用时间戳作为ID以确保唯一性
                return null;
            }).when(productMapper).insertProduct(any(Product.class));

            // 创建3个产品，超过缓存大小限制
            for (int i = 0; i < 3; i++) {
                request.setName("Test Product " + i);
                productService.createProduct(request);
            }

            verify(productMapper, times(3)).insertProduct(any(Product.class));
        }
    }

    @Nested
    class ListProductsTests {

        @Test
        void testListProducts_EmptyCache_LoadFromDatabase() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);

            List<Product> mockProducts = createMockProducts(5);
            lenient().when(productMapper.findAll()).thenReturn(mockProducts);
            lenient().when(productMapper.findWithPagination(null, 0, 10)).thenReturn(mockProducts);
            lenient().when(productMapper.countTotal(null)).thenReturn(5L);

            PageResponse<Product> result = productService.listProducts(request);

            assertNotNull(result);
            assertEquals(5, result.getRecords().size());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());

            verify(productMapper).findAll();
        }

        @Test
        void testListProducts_WithNameFilter() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);
            request.setName("Product 1");

            List<Product> mockProducts = createMockProducts(5);
            when(productMapper.findAll()).thenReturn(mockProducts);

            PageResponse<Product> result = productService.listProducts(request);

            assertNotNull(result);
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());

            verify(productMapper).findAll();
        }

        @Test
        void testListProducts_Pagination() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(2);
            request.setPageSize(3);

            List<Product> mockProducts = createMockProducts(10);
            when(productMapper.findAll()).thenReturn(mockProducts);

            PageResponse<Product> result = productService.listProducts(request);

            assertNotNull(result);
            assertEquals(2, result.getPageNum());
            assertEquals(3, result.getPageSize());

            verify(productMapper).findAll();
        }

        @Test
        void testListProducts_LargeCacheSize_UseDatabase() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);

            // 创建超过配置缓存大小的产品数量
            List<Product> mockProducts = createMockProducts(1001);
            lenient().when(productMapper.findAll()).thenReturn(mockProducts);
            lenient().when(productMapper.findWithPagination(null, 0, 10)).thenReturn(mockProducts.subList(0, 10));
            lenient().when(productMapper.countTotal(null)).thenReturn(1001L);

            PageResponse<Product> result = productService.listProducts(request);

            assertNotNull(result);
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());

            verify(productMapper).findAll();
            verify(productMapper).findWithPagination(null, 0, 10);
            verify(productMapper).countTotal(null);
        }

        @Test
        void testListProducts_ConfigurableCacheSize() {
            // 设置较小的缓存大小进行测试
            ReflectionTestUtils.setField(productService, "productCacheMaxSize", 5);

            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);

            // 创建超过配置缓存大小的产品数量
            List<Product> mockProducts = createMockProducts(10);
            lenient().when(productMapper.findAll()).thenReturn(mockProducts);
            lenient().when(productMapper.findWithPagination(null, 0, 10)).thenReturn(mockProducts);
            lenient().when(productMapper.countTotal(null)).thenReturn(10L);

            PageResponse<Product> result = productService.listProducts(request);

            assertNotNull(result);
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());

            verify(productMapper).findAll();
            verify(productMapper).findWithPagination(null, 0, 10);
            verify(productMapper).countTotal(null);
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
