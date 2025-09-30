package br.com.erudio.unittests.services;

import br.com.erudio.data.dto.PersonDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import br.com.erudio.services.PersonService;
import br.com.erudio.unittests.mapper.mocks.MockPerson;
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
class PersonServiceTest {

    MockPerson input;

    @InjectMocks
    private PersonService service;

    @Mock
    PersonRepository personRepository;

    @Mock
    PagedResourcesAssembler<PersonDTO> assembler;

    @BeforeEach
    void setUp() {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // CORREÇÃO: Verificação de link mais robusta
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("/api/person/v1/1"));

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void create() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        when(personRepository.save(person)).thenReturn(persisted);

        var result = service.create(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // CORREÇÃO: Verificação de link mais robusta
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("/api/person/v1/1"));

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testCreateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(person)).thenReturn(persisted);

        var result = service.update(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // CORREÇÃO: Verificação de link mais robusta
        assertTrue(result.hasLink("self"));
        assertTrue(result.getRequiredLink("self").getHref().contains("/api/person/v1/1"));

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void delete() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        service.delete(1L);
        verify(personRepository, times(1)).findById(anyLong());
        verify(personRepository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void findAll() {
        // Arrange
        List<Person> entityList = input.mockEntityList();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "firstName"));
        Page<Person> personPage = new PageImpl<>(entityList, pageable, entityList.size());

        when(personRepository.findAll(any(Pageable.class))).thenReturn(personPage);

        // Mocking the assembler behavior
        when(assembler.toModel(any(), any(Link.class))).thenAnswer(invocation -> {
            Page<PersonDTO> dtoPage = invocation.getArgument(0);
            Link selfLink = invocation.getArgument(1);

            List<EntityModel<PersonDTO>> entityModels = dtoPage.getContent().stream()
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

        List<EntityModel<PersonDTO>> content = result.getContent().stream().toList();

        var personOne = content.get(1).getContent();
        assertNotNull(personOne);
        assertNotNull(personOne.getId());
        assertNotNull(personOne.getLinks());

        // CORREÇÃO: Verificação de link mais robusta
        assertTrue(personOne.hasLink("self"));
        assertTrue(personOne.getRequiredLink("self").getHref().contains("/api/person/v1/1"));

        assertEquals("Address Test1", personOne.getAddress());
        assertEquals("First Name Test1", personOne.getFirstName());
        assertEquals("Last Name Test1", personOne.getLastName());
        assertEquals("Female", personOne.getGender());

        var personFour = content.get(4).getContent();
        assertNotNull(personFour);
        assertNotNull(personFour.getId());
        assertNotNull(personFour.getLinks());

        // CORREÇÃO: Verificação de link mais robusta
        assertTrue(personFour.hasLink("self"));
        assertTrue(personFour.getRequiredLink("self").getHref().contains("/api/person/v1/4"));

        assertEquals("Address Test4", personFour.getAddress());
        assertEquals("First Name Test4", personFour.getFirstName());
        assertEquals("Last Name Test4", personFour.getLastName());
        assertEquals("Male", personFour.getGender());

        var personSeven = content.get(7).getContent();
        assertNotNull(personSeven);
        assertNotNull(personSeven.getId());
        assertNotNull(personSeven.getLinks());

        // CORREÇÃO: Verificação de link mais robusta
        assertTrue(personSeven.hasLink("self"));
        assertTrue(personSeven.getRequiredLink("self").getHref().contains("/api/person/v1/7"));

        assertEquals("Address Test7", personSeven.getAddress());
        assertEquals("First Name Test7", personSeven.getFirstName());
        assertEquals("Last Name Test7", personSeven.getLastName());
        assertEquals("Female", personSeven.getGender());
    }
}