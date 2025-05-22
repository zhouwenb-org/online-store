package com.example.onlinestore.service.impl;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapperMock;

    @Mock
    private StringRedisTemplate stringRedisTemplateMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private ValueOperations<String, String> valueOperationsMock;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final Duration PRODUCT_CACHE_TTL = Duration.ofHours(1);

    @BeforeEach
    void setUp() {
        // This setup is crucial for mocking Redis operations
        when(stringRedisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
    }

    private CreateProductRequest createSampleCreateProductRequest() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setCategory("Test Category");
        request.setPrice(BigDecimal.valueOf(100.00));
        return request;
    }

    private Product createSampleProduct(Long id, CreateProductRequest requestDetails) {
        Product product = new Product();
        product.setId(id);
        product.setName(requestDetails.getName());
        product.setCategory(requestDetails.getCategory());
        product.setPrice(requestDetails.getPrice());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    @Test
    void testCreateProduct_SuccessAndCache() throws JsonProcessingException {
        CreateProductRequest request = createSampleCreateProductRequest();
        Product productToCreate = new Product(); // Product that will be "created"
        productToCreate.setName(request.getName());
        productToCreate.setCategory(request.getCategory());
        productToCreate.setPrice(request.getPrice());
        // Assume ID is set by productMapper.insertProduct
        Long generatedId = 1L;
        String productJson = "{\"id\":1,\"name\":\"Test Product\"}";

        // Mocking productMapper to simulate ID generation
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(generatedId);
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            return null; // void method
        }).when(productMapperMock).insertProduct(any(Product.class));

        when(objectMapperMock.writeValueAsString(any(Product.class))).thenReturn(productJson);

        Product createdProduct = productServiceImpl.createProduct(request);

        assertNotNull(createdProduct);
        assertEquals(generatedId, createdProduct.getId());
        assertEquals(request.getName(), createdProduct.getName());

        verify(productMapperMock, times(1)).insertProduct(any(Product.class));
        verify(objectMapperMock, times(1)).writeValueAsString(createdProduct);
        verify(stringRedisTemplateMock.opsForValue(), times(1))
                .set(eq(PRODUCT_CACHE_PREFIX + generatedId), eq(productJson), eq(PRODUCT_CACHE_TTL));
    }

    @Test
    void testCreateProduct_CacheError() throws JsonProcessingException {
        CreateProductRequest request = createSampleCreateProductRequest();
        Long generatedId = 2L;

        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(generatedId);
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            return null;
        }).when(productMapperMock).insertProduct(any(Product.class));

        when(objectMapperMock.writeValueAsString(any(Product.class)))
                .thenThrow(new JsonProcessingException("Test JSON error") {});

        Product createdProduct = productServiceImpl.createProduct(request);

        assertNotNull(createdProduct);
        assertEquals(generatedId, createdProduct.getId()); // Product creation should still succeed

        verify(productMapperMock, times(1)).insertProduct(any(Product.class));
        verify(objectMapperMock, times(1)).writeValueAsString(any(Product.class));
        // Verify that set was NOT called on Redis due to the error
        verify(stringRedisTemplateMock.opsForValue(), never())
                .set(anyString(), anyString(), any(Duration.class));
        // Further verification could involve a log capturing appender if strict log message testing is needed
    }

    @Test
    void testListProducts_SuccessAndCacheWarming() throws JsonProcessingException {
        ProductPageRequest request = new ProductPageRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        Product product1 = createSampleProduct(101L, createSampleCreateProductRequest());
        Product product2 = createSampleProduct(102L, createSampleCreateProductRequest());
        List<Product> sampleProducts = Arrays.asList(product1, product2);
        String product1Json = "{\"id\":101,\"name\":\"Test Product\"}";
        String product2Json = "{\"id\":102,\"name\":\"Test Product\"}";

        when(productMapperMock.findWithPagination(null, 0, 10)).thenReturn(sampleProducts);
        when(productMapperMock.countTotal(null)).thenReturn((long) sampleProducts.size());

        when(objectMapperMock.writeValueAsString(product1)).thenReturn(product1Json);
        when(objectMapperMock.writeValueAsString(product2)).thenReturn(product2Json);

        PageResponse<Product> response = productServiceImpl.listProducts(request);

        assertNotNull(response);
        assertEquals(sampleProducts.size(), response.getRecords().size());
        assertEquals(sampleProducts.size(), response.getTotal());

        verify(productMapperMock, times(1)).findWithPagination(null, 0, 10);
        verify(productMapperMock, times(1)).countTotal(null);

        // Verify caching for each product
        verify(objectMapperMock, times(1)).writeValueAsString(product1);
        verify(stringRedisTemplateMock.opsForValue(), times(1))
                .set(eq(PRODUCT_CACHE_PREFIX + product1.getId()), eq(product1Json), eq(PRODUCT_CACHE_TTL));

        verify(objectMapperMock, times(1)).writeValueAsString(product2);
        verify(stringRedisTemplateMock.opsForValue(), times(1))
                .set(eq(PRODUCT_CACHE_PREFIX + product2.getId()), eq(product2Json), eq(PRODUCT_CACHE_TTL));
    }
    
    @Test
    void testListProducts_CacheWarmingError() throws JsonProcessingException {
        ProductPageRequest request = new ProductPageRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        Product product1 = createSampleProduct(201L, createSampleCreateProductRequest());
        Product product2Error = createSampleProduct(202L, createSampleCreateProductRequest()); // This one will cause error
        Product product3 = createSampleProduct(203L, createSampleCreateProductRequest());
        List<Product> sampleProducts = Arrays.asList(product1, product2Error, product3);
        
        String product1Json = "{\"id\":201,\"name\":\"Test Product\"}";
        // product2Json will not be generated due to error
        String product3Json = "{\"id\":203,\"name\":\"Test Product\"}";


        when(productMapperMock.findWithPagination(null, 0, 10)).thenReturn(sampleProducts);
        when(productMapperMock.countTotal(null)).thenReturn((long) sampleProducts.size());

        when(objectMapperMock.writeValueAsString(product1)).thenReturn(product1Json);
        when(objectMapperMock.writeValueAsString(product2Error)) // Mock error for product2
                .thenThrow(new JsonProcessingException("Simulated JSON error for product 202") {});
        when(objectMapperMock.writeValueAsString(product3)).thenReturn(product3Json);


        PageResponse<Product> response = productServiceImpl.listProducts(request);

        assertNotNull(response);
        assertEquals(sampleProducts.size(), response.getRecords().size()); // DB fetch should still work
        assertEquals(sampleProducts.size(), response.getTotal());

        verify(productMapperMock, times(1)).findWithPagination(null, 0, 10);
        verify(productMapperMock, times(1)).countTotal(null);

        // Verify caching for product1 (should succeed)
        verify(objectMapperMock, times(1)).writeValueAsString(product1);
        verify(stringRedisTemplateMock.opsForValue(), times(1))
                .set(eq(PRODUCT_CACHE_PREFIX + product1.getId()), eq(product1Json), eq(PRODUCT_CACHE_TTL));

        // Verify attempt to cache product2Error (objectMapper is called, but set is not)
        verify(objectMapperMock, times(1)).writeValueAsString(product2Error);
        
        // Verify caching for product3 (should succeed as the loop continues)
        verify(objectMapperMock, times(1)).writeValueAsString(product3);
        verify(stringRedisTemplateMock.opsForValue(), times(1))
                .set(eq(PRODUCT_CACHE_PREFIX + product3.getId()), eq(product3Json), eq(PRODUCT_CACHE_TTL));
                
        // Crucially, verify that 'set' was NOT called for product2Error because objectMapper threw an exception for it.
        // The current implementation logs the error and continues, so set would be called for product1 and product3
        // but not for product2Error itself.
        // The number of successful 'set' calls should be 2 (for product1 and product3)
        verify(stringRedisTemplateMock.opsForValue(), times(2)) // product1 and product3
            .set(anyString(), anyString(), eq(PRODUCT_CACHE_TTL));
        
        // Explicitly check that product2Error was not set.
         verify(stringRedisTemplateMock.opsForValue(), never())
                .set(eq(PRODUCT_CACHE_PREFIX + product2Error.getId()), anyString(), eq(PRODUCT_CACHE_TTL));
    }
}
