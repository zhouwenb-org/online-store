package com.example.onlinestore.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {

    @Nested
    class GetterSetterTests {

        @Test
        void testRecordsGetterSetter() {
            PageResponse<String> pageResponse = new PageResponse<>();
            List<String> records = new ArrayList<>();
            records.add("item1");
            records.add("item2");

            pageResponse.setRecords(records);

            assertEquals(records, pageResponse.getRecords());
            assertEquals(2, pageResponse.getRecords().size());
        }

        @Test
        void testTotalGetterSetter() {
            PageResponse<String> pageResponse = new PageResponse<>();
            long total = 100L;

            pageResponse.setTotal(total);

            assertEquals(total, pageResponse.getTotal());
        }

        @Test
        void testPageNumGetterSetter() {
            PageResponse<String> pageResponse = new PageResponse<>();
            int pageNum = 5;

            pageResponse.setPageNum(pageNum);

            assertEquals(pageNum, pageResponse.getPageNum());
        }

        @Test
        void testPageSizeGetterSetter() {
            PageResponse<String> pageResponse = new PageResponse<>();
            int pageSize = 20;

            pageResponse.setPageSize(pageSize);

            assertEquals(pageSize, pageResponse.getPageSize());
        }
    }

    @Nested
    class ObjectTests {

        @Test
        void testPageResponseCreation() {
            PageResponse<String> pageResponse = new PageResponse<>();

            assertNotNull(pageResponse);
            assertNull(pageResponse.getRecords());
            assertEquals(0, pageResponse.getTotal());
            assertEquals(0, pageResponse.getPageNum());
            assertEquals(0, pageResponse.getPageSize());
        }

        @Test
        void testPageResponseWithAllFields() {
            PageResponse<String> pageResponse = new PageResponse<>();
            List<String> records = new ArrayList<>();
            records.add("item1");
            long total = 100L;
            int pageNum = 5;
            int pageSize = 20;

            pageResponse.setRecords(records);
            pageResponse.setTotal(total);
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);

            assertEquals(records, pageResponse.getRecords());
            assertEquals(total, pageResponse.getTotal());
            assertEquals(pageNum, pageResponse.getPageNum());
            assertEquals(pageSize, pageResponse.getPageSize());
        }

        @Test
        void testEmptyRecords() {
            PageResponse<String> pageResponse = new PageResponse<>();
            List<String> emptyRecords = new ArrayList<>();

            pageResponse.setRecords(emptyRecords);

            assertNotNull(pageResponse.getRecords());
            assertTrue(pageResponse.getRecords().isEmpty());
        }

        @Test
        void testNullRecords() {
            PageResponse<String> pageResponse = new PageResponse<>();

            pageResponse.setRecords(null);

            assertNull(pageResponse.getRecords());
        }
    }
}
