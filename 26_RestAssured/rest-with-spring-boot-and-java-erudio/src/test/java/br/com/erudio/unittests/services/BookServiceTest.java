package br.com.erudio.unittests.services;

import br.com.erudio.data.dto.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.services.BookService;
import br.com.erudio.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    MockBook input;

    @InjectMocks
    private BookService service;

    @Mock
    BookRepository repository;

    @Mock
    PagedResourcesAssembler<BookDTO> assembler;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // Asserção de link simplificada e robusta
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("/api/book/v1/1"));

        assertEquals("Some Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void create() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(repository.save(any(Book.class))).thenReturn(persisted);

        var result = service.create(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // Asserção de link simplificada e robusta
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("/api/book/v1/1"));

        assertEquals("Some Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testCreateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> service.create(null));

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(persisted);

        var result = service.update(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // Asserção de link simplificada e robusta
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("/api/book/v1/1"));

        assertEquals("Some Author1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testUpdateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> service.update(null));

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.delete(1L);
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        // Arrange
        List<Book> entityList = input.mockEntityList();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Book> bookPage = new PageImpl<>(entityList, pageable, entityList.size());

        when(repository.findAll(any(Pageable.class))).thenReturn(bookPage);

        // Mocking the assembler behavior
        when(assembler.toModel(any(), any(Link.class))).thenAnswer(invocation -> {
            Page<BookDTO> dtoPage = invocation.getArgument(0);
            Link selfLink = invocation.getArgument(1);

            List<EntityModel<BookDTO>> entityModels = dtoPage.getContent().stream()
                    .map(EntityModel::of)
                    .collect(Collectors.toList());

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                    dtoPage.getSize(),
                    dtoPage.getNumber(),
                    dtoPage.getTotalElements(),
                    dtoPage.getTotalPages());

            return PagedModel.of(entityModels, metadata, selfLink);
        });

        // Act
        var result = service.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(14, result.getMetadata().getTotalElements());

        List<EntityModel<BookDTO>> content = result.getContent().stream().toList();

        var bookOne = content.get(1).getContent();
        assertNotNull(bookOne);
        assertNotNull(bookOne.getId());
        assertNotNull(bookOne.getLinks());

        assertTrue(bookOne.hasLink("self"));
        assertTrue(bookOne.getRequiredLink("self").getHref().contains("/api/book/v1/1"));

        assertEquals("Some Author1", bookOne.getAuthor());
        assertEquals(25D, bookOne.getPrice());
        assertEquals("Some Title1", bookOne.getTitle());
        assertNotNull(bookOne.getLaunchDate());

        var bookFour = content.get(4).getContent();
        assertNotNull(bookFour);
        assertNotNull(bookFour.getId());
        assertNotNull(bookFour.getLinks());

        assertTrue(bookFour.hasLink("self"));
        assertTrue(bookFour.getRequiredLink("self").getHref().contains("/api/book/v1/4"));

        assertEquals("Some Author4", bookFour.getAuthor());
        assertEquals(25D, bookFour.getPrice());
        assertEquals("Some Title4", bookFour.getTitle());
        assertNotNull(bookFour.getLaunchDate());

        var bookSeven = content.get(7).getContent();
        assertNotNull(bookSeven);
        assertNotNull(bookSeven.getId());
        assertNotNull(bookSeven.getLinks());

        assertTrue(bookSeven.hasLink("self"));
        assertTrue(bookSeven.getRequiredLink("self").getHref().contains("/api/book/v1/7"));

        assertEquals("Some Author7", bookSeven.getAuthor());
        assertEquals(25D, bookSeven.getPrice());
        assertEquals("Some Title7", bookSeven.getTitle());
        assertNotNull(bookSeven.getLaunchDate());
    }
}