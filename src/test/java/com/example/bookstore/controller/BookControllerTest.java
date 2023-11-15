package com.example.bookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
class BookControllerTest {
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
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/categories/create-test-categories.sql"));
    }

    @Test
    @DisplayName("Verify create book works")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("New book");
        requestDto.setAuthor("Author");
        requestDto.setIsbn("22222222");
        requestDto.setPrice(BigDecimal.TEN);
        requestDto.setCategoryIds(Set.of(1L));

        BookDto expected = new BookDto();
        expected.setAuthor(requestDto.getAuthor());
        expected.setTitle(requestDto.getTitle());
        expected.setIsbn(requestDto.getIsbn());
        expected.setCategoryIds(requestDto.getCategoryIds());
        expected.setPrice(requestDto.getPrice());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Verify create book with invalid request dto results in HTTP 404 NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_InvalidRequestDtoWithCategoryNotFound_NotFound() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("New book");
        requestDto.setAuthor("Author");
        requestDto.setIsbn("2222222");
        requestDto.setPrice(BigDecimal.TEN);
        requestDto.setCategoryIds(Set.of(3L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify create book with forbidden access results in HTTP 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void createBook_ForbiddenAccess_Forbidden() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("New book");
        requestDto.setAuthor("Author");
        requestDto.setIsbn("22222222");
        requestDto.setPrice(BigDecimal.TEN);
        requestDto.setCategoryIds(Set.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify update a book by id works")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/create-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_ValidIdAndRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("33333333");
        requestDto.setPrice(BigDecimal.valueOf(20.0));
        requestDto.setCategoryIds(Set.of(1L));

        BookDto expected = new BookDto();
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setCategoryIds(requestDto.getCategoryIds());
        expected.setId(1L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put("/api/books/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify update a book by id with invalid id results in HTTP 404 NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBook_InvalidId_NotFound() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("33333333");
        requestDto.setPrice(BigDecimal.valueOf(20.0));
        requestDto.setCategoryIds(Set.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/books/{id}", 100L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify update a book by id with invalid request dto results in HTTP 404 NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/create-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_InvalidRequestDtoCategoryNotFound_NotFound() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("33333333");
        requestDto.setPrice(BigDecimal.valueOf(20.0));
        requestDto.setCategoryIds(Set.of(10L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/books/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify update a book by id with unauthorized user results in HTTP 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void updateBook_ForbiddenAccess_Forbidden() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("33333333");
        requestDto.setPrice(BigDecimal.valueOf(20.0));
        requestDto.setCategoryIds(Set.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/books/{id}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Verify get all books works")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/books/create-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllBooks_retrievesAllBooks_Success() throws Exception {
        // Given
        BookDto firstBook = new BookDto();
        firstBook.setId(1L);
        firstBook.setTitle("Test book");
        firstBook.setAuthor("Test test");
        firstBook.setIsbn("1111");
        firstBook.setPrice(BigDecimal.TEN);
        firstBook.setCategoryIds(Set.of(1L));

        BookDto secondBook = new BookDto();
        secondBook.setId(2L);
        secondBook.setTitle("Test book");
        secondBook.setAuthor("Test test");
        secondBook.setIsbn("2222");
        secondBook.setPrice(BigDecimal.TEN);
        secondBook.setCategoryIds(Set.of(1L));

        List<BookDto> expected = new ArrayList<>();
        expected.add(firstBook);
        expected.add(secondBook);

        // When
        MvcResult result = mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        EqualsBuilder.reflectionEquals(expected.get(0), actual[0]);
        EqualsBuilder.reflectionEquals(expected.get(1), actual[1]);
    }

    @Test
    @DisplayName("Verify get all books with unauthorized user results in HTTP 401 Unauthorized")
    public void getAllBooks_UnauthorizedAccess_Unauthorized() throws Exception {
        // When
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Verify get book by id works")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/books/create-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_ValidId_Success() throws Exception {
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Test book");
        expected.setAuthor("Test test");
        expected.setIsbn("1111");
        expected.setPrice(BigDecimal.TEN);
        expected.setCategoryIds(Set.of(1L));

        // When
        MvcResult result = mockMvc.perform(get("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify get book by id with invalid id results in HTTP 404 NotFound")
    @WithMockUser(username = "user", roles = {"USER"})
    void getBookById_InvalidId_NotFound() throws Exception {
        // When
        mockMvc.perform(get("/api/books/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify get book by id with unauthorized user results in HTTP 401 Unauthorized")
    void getBookById_UnauthorisedAccess_Unauthorised() throws Exception {
        // When
        mockMvc.perform(get("/api/books/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Verify delete book by id works")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/create-test-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_Success() throws Exception {
        // When
        mockMvc.perform(delete("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Verify delete book by id with forbidden access results in HTTP 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteBook_ForbiddenAccess_Forbidden() throws Exception {
        // When
        mockMvc.perform(delete("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
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
                new ClassPathResource("database/books/delete-all-test-books.sql"));
        ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/categories/delete-all-test-categories.sql"));
    }
}
