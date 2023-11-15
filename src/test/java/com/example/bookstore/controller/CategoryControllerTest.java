package com.example.bookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.CategoryRequestDto;
import com.example.bookstore.dto.CategoryResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Verify create category works")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/delete-all-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Test category");
        requestDto.setDescription("Test");

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setName(requestDto.getName());
        expected.setDescription(requestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        CategoryResponseDto actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Verify create category with invalid request dto results in HTTP 400 BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_InvalidRequestDto_400BadRequest() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setDescription("Test");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify create category with forbidden access results in HTTP 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void createCategory_ForbiddenAccess_403Forbidden() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Test category");
        requestDto.setDescription("Test");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify get all categories works")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/categories/create-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-all-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllCategories_retrievesAllCategories_Success() throws Exception {
        // Given
        CategoryResponseDto firstDto = new CategoryResponseDto();
        firstDto.setName("Test Category");
        firstDto.setDescription("Test");
        firstDto.setId(1L);

        CategoryResponseDto secondDto = new CategoryResponseDto();
        secondDto.setName("New Test Category");
        secondDto.setDescription("Test");
        secondDto.setId(2L);

        List<CategoryResponseDto> expected = new ArrayList<>();
        expected.add(firstDto);
        expected.add(secondDto);

        // When
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponseDto[] actual = objectMapper.readValue(result.getResponse()
                        .getContentAsByteArray(), CategoryResponseDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        EqualsBuilder.reflectionEquals(expected.get(0), actual[0]);
        EqualsBuilder.reflectionEquals(expected.get(1), actual[1]);
    }

    @Test
    @DisplayName("Verify get categories with unauthorized user results in HTTP 401 Unauthorized")
    public void getAllCategories_UnauthorizedAccess_Unauthorized() throws Exception {
        // When
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Verify get category by id works")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/categories/create-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-all-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_ValidId_Success() throws Exception {
        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setName("Test Category");
        expected.setDescription("Test");
        expected.setId(1L);

        // When
        MvcResult result = mockMvc.perform(get("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryResponseDto actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify get category by id with invalid id results in HTTP 404 NotFound")
    @WithMockUser(username = "user", roles = {"USER"})
    void getCategoryById_InvalidId_NotFound() throws Exception {
        // When
        mockMvc.perform(get("/api/categories/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify get book by id with unauthorized user results in HTTP 401 Unauthorized")
    void getCategoryById_UnauthorisedAccess_Unauthorised() throws Exception {
        // When
        mockMvc.perform(get("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Verify update a category by id works")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/create-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-all-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_ValidIdAndRequestDto_Success() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Updated Category");
        requestDto.setDescription("Updated");

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setName(requestDto.getName());
        expected.setDescription(requestDto.getDescription());
        expected.setId(1L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put("/api/categories/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryResponseDto actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify update a category by id with invalid id results in HTTP 404 NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_InvalidId_NotFound() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Updated Category");
        requestDto.setDescription("Updated");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/categories/{id}", 100L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify update category by id with invalid request dto results in "
             + "HTTP 400 BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/create-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-all-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_InvalidRequestDto_BadRequest() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verify update category by id with unauthorized user results in "
            + "HTTP 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void updateCategory_ForbiddenAccess_Forbidden() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Updated Category");
        requestDto.setDescription("Updated");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify delete category by id works")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/create-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-all-test-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_Success() throws Exception {
        // When
        mockMvc.perform(delete("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Verify delete category by id with forbidden access results in HTTP 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteBook_ForbiddenAccess_Forbidden() throws Exception {
        // When
        mockMvc.perform(delete("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify get books by category id works")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = {"classpath:database/categories/create-test-categories.sql",
            "classpath:database/books/create-test-books.sql",
            "classpath:database/categories/create-test-books-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/categories/delete-all-test-categories.sql",
            "classpath:database/books/delete-all-test-books.sql",
            "classpath:database/categories/delete-all-test-books-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId() throws Exception {
        // Given
        BookDtoWithoutCategoryIds firstBook = new BookDtoWithoutCategoryIds();
        firstBook.setId(1L);
        firstBook.setTitle("Test book");
        firstBook.setAuthor("Test test");
        firstBook.setIsbn("1111");
        firstBook.setPrice(BigDecimal.TEN);

        BookDtoWithoutCategoryIds secondBook = new BookDtoWithoutCategoryIds();
        secondBook.setId(2L);
        secondBook.setTitle("Test book");
        secondBook.setAuthor("Test test");
        secondBook.setIsbn("2222");
        secondBook.setPrice(BigDecimal.TEN);

        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(firstBook);
        expected.add(secondBook);

        // When
        MvcResult result = mockMvc.perform(get("/api/categories/{id}/books", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(result.getResponse()
                        .getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        EqualsBuilder.reflectionEquals(expected.get(0), actual[0]);
        EqualsBuilder.reflectionEquals(expected.get(1), actual[1]);
    }

    @Test
    @DisplayName("Verify get books by category id with unauthorized user results in "
            + "HTTP 401 Unauthorized")
    public void getBooksByCategoryId_UnauthorizedAccess_Unauthorized() throws Exception {
        // When
        mockMvc.perform(get("/api/categories/{id}/books", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    private static void tearDown(DataSource dataSource) {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/categories/delete-all-test-books-categories.sql"));
        ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/books/delete-all-test-books.sql"));
        ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/categories/delete-all-test-categories.sql"));
    }
}
