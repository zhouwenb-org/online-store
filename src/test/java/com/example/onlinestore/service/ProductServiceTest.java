package com.example.onlinestore.service;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.dto.UpdateProductRequest;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

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
        void createProduct_Success() {
            // Given
            // 模拟产品缓存为空
            when(productMapper.findAll()).thenReturn(Arrays.asList());

            // When
            Product result = productService.createProduct(createRequest);

            // Then
            assertNotNull(result);
            assertEquals("新商品", result.getName());
            assertEquals("家居用品", result.getCategory());
            assertEquals(new BigDecimal("199.99"), result.getPrice());
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());

            verify(productMapper).insertProduct(any(Product.class));
        }

        @Test
        void createProduct_NullRequest() {
            // When & Then
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> productService.createProduct(null)
            );
        }

        @Test
        void createProduct_CacheManagement() {
            // Given
            List<Product> existingProducts = Arrays.asList(testProduct);
            when(productMapper.findAll()).thenReturn(existingProducts);

            // When
            Product result = productService.createProduct(createRequest);

            // Then
            assertNotNull(result);
            verify(productMapper).insertProduct(any(Product.class));
        }
    }

    @Nested
    class ListProductsTests {

        @Test
        void listProducts_Success() {
            // Given
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);
            request.setName("测试");

            List<Product> mockProducts = Arrays.asList(testProduct);
            when(productMapper.findWithPagination(
                eq("测试"), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(mockProducts);
            when(productMapper.countTotal(
                eq("测试"), isNull(), isNull(), isNull()))
                .thenReturn(1L);

            // When
            PageResponse<Product> result = productService.listProducts(request);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());
            assertEquals(testProduct.getId(), result.getRecords().get(0).getId());
        }

        @Test
        void listProducts_WithAllFilters() {
            // Given
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(2);
            request.setPageSize(5);
            request.setName("商品");
            request.setCategory("电子产品");
            request.setMinPrice(new BigDecimal("100.00"));
            request.setMaxPrice(new BigDecimal("500.00"));

            List<Product> mockProducts = Arrays.asList(testProduct);
            when(productMapper.findWithPagination(
                eq("商品"), eq("电子产品"), 
                eq(new BigDecimal("100.00")), eq(new BigDecimal("500.00")), 
                eq(5), eq(5)))
                .thenReturn(mockProducts);
            when(productMapper.countTotal(
                eq("商品"), eq("电子产品"), 
                eq(new BigDecimal("100.00")), eq(new BigDecimal("500.00"))))
                .thenReturn(1L);

            // When
            PageResponse<Product> result = productService.listProducts(request);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertEquals(2, result.getPageNum());
            assertEquals(5, result.getPageSize());
        }

        @Test
        void listProducts_EmptyResult() {
            // Given
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);

            when(productMapper.findWithPagination(
                isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(Arrays.asList());
            when(productMapper.countTotal(
                isNull(), isNull(), isNull(), isNull()))
                .thenReturn(0L);

            // When
            PageResponse<Product> result = productService.listProducts(request);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getRecords().size());
            assertEquals(0L, result.getTotal());
        }
    }

    @Nested
    class GetProductByIdTests {

        @Test
        void getProductById_Success_FromCache() {
            // Given
            // 模拟缓存中有数据
            productService.createProduct(createRequest); // 这会把产品加入缓存
            when(productMapper.findById(1L)).thenReturn(testProduct);

            // When
            Product result = productService.getProductById(1L);

            // Then
            assertNotNull(result);
            assertEquals(testProduct.getId(), result.getId());
            assertEquals(testProduct.getName(), result.getName());
        }

        @Test
        void getProductById_Success_FromDatabase() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);

            // When
            Product result = productService.getProductById(1L);

            // Then
            assertNotNull(result);
            assertEquals(testProduct.getId(), result.getId());
            verify(productMapper).findById(1L);
        }

        @Test
        void getProductById_NotFound() {
            // Given
            when(productMapper.findById(1L)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductById(1L)
            );
            assertEquals("商品不存在，ID：1", exception.getMessage());
        }

        @Test
        void getProductById_NullId() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductById(null)
            );
            assertEquals("商品ID不能为空", exception.getMessage());
        }
    }

    @Nested
    class UpdateProductTests {

        @Test
        void updateProduct_Success() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);
            when(productMapper.updateById(eq(1L), eq("更新商品"), isNull(), 
                eq(new BigDecimal("399.99")), any(LocalDateTime.class)))
                .thenReturn(1);

            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setName("更新商品");
            updatedProduct.setCategory("电子产品");
            updatedProduct.setPrice(new BigDecimal("399.99"));
            updatedProduct.setUpdatedAt(LocalDateTime.now());

            when(productMapper.findById(1L)).thenReturn(updatedProduct);

            // When
            Product result = productService.updateProduct(1L, updateRequest);

            // Then
            assertNotNull(result);
            assertEquals("更新商品", result.getName());
            assertEquals(new BigDecimal("399.99"), result.getPrice());
            
            verify(productMapper).updateById(eq(1L), eq("更新商品"), isNull(), 
                eq(new BigDecimal("399.99")), any(LocalDateTime.class));
        }

        @Test
        void updateProduct_NotFound() {
            // Given
            when(productMapper.findById(1L)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, updateRequest)
            );
            assertEquals("商品不存在，ID：1", exception.getMessage());
        }

        @Test
        void updateProduct_NoUpdates() {
            // Given
            UpdateProductRequest emptyRequest = new UpdateProductRequest();

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, emptyRequest)
            );
            assertEquals("更新请求不能为空且至少包含一个待更新字段", exception.getMessage());
        }

        @Test
        void updateProduct_UpdateFailed() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);
            when(productMapper.updateById(any(), any(), any(), any(), any()))
                .thenReturn(0); // 更新失败

            // When & Then
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.updateProduct(1L, updateRequest)
            );
            assertEquals("更新商品失败，ID：1", exception.getMessage());
        }
    }

    @Nested
    class DeleteProductTests {

        @Test
        void deleteProductById_Success() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);
            when(productMapper.deleteById(1L)).thenReturn(1);

            // When
            assertDoesNotThrow(() -> productService.deleteProductById(1L));

            // Then
            verify(productMapper).findById(1L);
            verify(productMapper).deleteById(1L);
        }

        @Test
        void deleteProductById_NotFound() {
            // Given
            when(productMapper.findById(1L)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.deleteProductById(1L)
            );
            assertEquals("商品不存在，ID：1", exception.getMessage());
        }

        @Test
        void deleteProductById_DeleteFailed() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);
            when(productMapper.deleteById(1L)).thenReturn(0);

            // When & Then
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.deleteProductById(1L)
            );
            assertEquals("删除商品失败，ID：1", exception.getMessage());
        }

        @Test
        void deleteProductById_NullId() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.deleteProductById(null)
            );
            assertEquals("商品ID不能为空", exception.getMessage());
        }
    }

    @Nested
    class GetAllCategoriesTests {

        @Test
        void getAllCategories_Success() {
            // Given
            List<String> mockCategories = Arrays.asList("电子产品", "家居用品", "服装");
            when(productMapper.findAllCategories()).thenReturn(mockCategories);

            // When
            List<String> result = productService.getAllCategories();

            // Then
            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.contains("电子产品"));
            assertTrue(result.contains("家居用品"));
            assertTrue(result.contains("服装"));
            
            verify(productMapper).findAllCategories();
        }

        @Test
        void getAllCategories_EmptyResult() {
            // Given
            when(productMapper.findAllCategories()).thenReturn(Arrays.asList());

            // When
            List<String> result = productService.getAllCategories();

            // Then
            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Nested
    class CacheManagementTests {

        @Test
        void cacheManagement_MaxCapacity() {
            // Given
            when(productMapper.findAll()).thenReturn(Arrays.asList());
            
            // When - 创建多个产品测试缓存容量
            for (int i = 0; i < 5; i++) {
                CreateProductRequest request = new CreateProductRequest();
                request.setName("商品" + i);
                request.setCategory("分类" + i);
                request.setPrice(new BigDecimal("100.00"));
                productService.createProduct(request);
            }

            // Then
            verify(productMapper, times(5)).insertProduct(any(Product.class));
        }
    }
}