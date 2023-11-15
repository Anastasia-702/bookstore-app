package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.CreateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Verify save book works")
    void save_ValidBookRequestDto_ReturnsBookDto() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Test author");
        requestDto.setTitle("Test title");
        requestDto.setIsbn("111111111");
        requestDto.setCategoryIds(Set.of(category.getId()));
        requestDto.setPrice(BigDecimal.TEN);

        Book book = new Book();
        book.setAuthor(requestDto.getAuthor());
        book.setTitle(requestDto.getTitle());
        book.setIsbn(requestDto.getIsbn());
        book.setCategories(Set.of(category));
        book.setPrice(requestDto.getPrice());

        BookDto expected = new BookDto();
        expected.setAuthor(book.getAuthor());
        expected.setTitle(book.getTitle());
        expected.setIsbn(book.getIsbn());
        expected.setCategoryIds(Set.of(category.getId()));
        expected.setPrice(book.getPrice());

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        // When
        BookDto actual = bookService.save(requestDto);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify save book with invalid request dto throws an exception")
    void save_InvalidRequestDtoCategoryNotFound_ThrowsException() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Test author");
        requestDto.setTitle("Test");
        requestDto.setIsbn("111111111");
        requestDto.setCategoryIds(Set.of(category.getId()));
        requestDto.setPrice(BigDecimal.TEN);

        Book book = new Book();
        book.setAuthor(requestDto.getAuthor());
        book.setTitle(requestDto.getTitle());
        book.setIsbn(requestDto.getIsbn());
        book.setCategories(Set.of(category));
        book.setPrice(requestDto.getPrice());

        BookDto bookDto = new BookDto();
        bookDto.setAuthor(book.getAuthor());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setCategoryIds(Set.of(category.getId()));
        bookDto.setPrice(book.getPrice());

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            bookService.save(requestDto);
        }, "EntityNotFoundException was expected");

        // Then
        Assertions.assertEquals("Category not found", exception.getLocalizedMessage());
        Mockito.verify(bookRepository, Mockito.times(0)).save(book);
    }

    @Test
    @DisplayName("Verify find all books works")
    void findAll_ValidPageable_ReturnsAllBookDtos() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        Book book = new Book();
        book.setAuthor("Test author");
        book.setTitle("Test");
        book.setIsbn("1111111");
        book.setCategories(Set.of(category));
        book.setPrice(BigDecimal.TEN);

        BookDto bookDto = new BookDto();
        bookDto.setAuthor(book.getAuthor());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setCategoryIds(Set.of(category.getId()));
        bookDto.setPrice(book.getPrice());

        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        List<BookDto> actual = bookService.findAll(pageable);

        // Then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(bookDto, actual.get(0));
    }

    @Test
    @DisplayName("Verify get book by id works")
    void getById_ValidId_ReturnsBookDto() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        Book book = new Book();
        book.setAuthor("Test author");
        book.setTitle("Test");
        book.setIsbn("1111111");
        book.setCategories(Set.of(category));
        book.setPrice(BigDecimal.TEN);

        BookDto expected = new BookDto();
        expected.setAuthor(book.getAuthor());
        expected.setTitle(book.getTitle());
        expected.setIsbn(book.getIsbn());
        expected.setCategoryIds(Set.of(category.getId()));
        expected.setPrice(book.getPrice());

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        // When
        BookDto actual = bookService.getById(book.getId());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(book.getId());
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify get book by id with invalid id throws exception")
    void getById_InvalidId_ThrowsEntityNotFoundException() {
        // Given
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            bookService.getById(1L);
        }, "EntityNotFoundException was expected");

        // Then
        Assertions.assertEquals("Book with id 1 not found.", exception.getLocalizedMessage());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Verify update book by id works")
    void update_ValidBookRequestDto_ReturnsBookDto() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Test Author");
        book.setTitle("Test");
        book.setIsbn("11111111");
        book.setCategories(Set.of(category));
        book.setPrice(BigDecimal.TEN);

        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Update Author");
        requestDto.setTitle("Update");
        requestDto.setIsbn("22222222");
        requestDto.setCategoryIds(Set.of(category.getId()));
        requestDto.setPrice(BigDecimal.ONE);

        BookDto expected = new BookDto();
        expected.setAuthor(requestDto.getAuthor());
        expected.setTitle(requestDto.getTitle());
        expected.setIsbn(requestDto.getIsbn());
        expected.setCategoryIds(Set.of(category.getId()));
        expected.setPrice(requestDto.getPrice());
        expected.setId(1L);

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        // When
        BookDto actual = bookService.update(book.getId(), requestDto);

        // Then
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        Assertions.assertEquals(expected.getIsbn(), actual.getIsbn());
        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify update book by id with invalid id throws exception")
    void update_InvalidBookId_ThrowsEntityNotFoundException() {
        // Given
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            bookService.update(1L, new CreateBookRequestDto());
        }, "EntityNotFoundException was expected");

        // Then
        Assertions.assertEquals("Can't update the book. Book with id 1 not found.",
                exception.getLocalizedMessage());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Verify update book by id with invalid request dtothrows exception")
    void update_InvalidRequestDtoCategoryNotFound_ThrowsException() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Test author");
        book.setTitle("Test");
        book.setIsbn("11111111");
        book.setCategories(Set.of(category));
        book.setPrice(BigDecimal.TEN);

        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Update author");
        requestDto.setTitle("Update title");
        requestDto.setIsbn("22222222");
        requestDto.setCategoryIds(Set.of(12L));
        requestDto.setPrice(BigDecimal.ONE);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            bookService.update(1L, requestDto);
        }, "EntityNotFoundException was expected");

        // Then
        Assertions.assertEquals("Category not found", exception.getLocalizedMessage());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Verify delete book by id works")
    void delete_ValidId() {
        // When
        bookService.delete(1L);
        // Then
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Verify find all books by category id works")
    void findAllByCategoryId() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        Book book = new Book();
        book.setAuthor("Test test");
        book.setTitle("Test");
        book.setIsbn("11111111");
        book.setPrice(BigDecimal.ONE);

        BookDtoWithoutCategoryIds bookDtoWithoutCategories = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategories.setAuthor(book.getAuthor());
        bookDtoWithoutCategories.setTitle(book.getTitle());
        bookDtoWithoutCategories.setIsbn(book.getIsbn());
        bookDtoWithoutCategories.setPrice(book.getPrice());

        Mockito.when(bookRepository.findAllByCategories_Id(category.getId()))
                .thenReturn(List.of(book));
        Mockito.when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategories);

        // When
        List<BookDtoWithoutCategoryIds> books = bookService.findAllByCategoryId(category.getId());

        // Then
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals(bookDtoWithoutCategories, books.get(0));
        Mockito.verify(bookRepository, Mockito.times(1)).findAllByCategories_Id(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}
