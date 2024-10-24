package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.Library;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.repositories.CheckableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CheckableServiceTest {

    //TODO: Inject dependencies and mocks
    @Autowired
    private CheckableService checkableService;

    @MockBean
    private CheckableRepository checkableRepository;

    private List<Checkable> checkables;

    @BeforeEach
    void init() {
        //Initialize test data
        checkables = new ArrayList<>();

        checkables.addAll(
                Arrays.asList(
                        new Media("1-0", "The White Whale", "Melvin H", MediaType.BOOK),
                        new Media("1-1", "The Sorcerer's Quest", "Ana T", MediaType.BOOK),
                        new Media("1-2", "When You're Gone", "Complaining at the Disco", MediaType.MUSIC),
                        new Media("1-3", "Nature Around the World", "DocuSpecialists", MediaType.VIDEO),
                        new ScienceKit("2-0", "Anatomy Model"),
                        new ScienceKit("2-1", "Robotics Kit"),
                        new Ticket("3-0", "Science Museum Tickets"),
                        new Ticket("3-1", "National Park Day Pass")
                )
        );
    }

    //TODO: Write Unit Tests for all CheckableService methods and possible Exceptions
    @Test
    void getAll() {
        when(checkableRepository.findAll()).thenReturn(checkables);
        List<Checkable> testCheckables = checkableService.getAll();
        assertEquals(checkables, testCheckables);
    }
    @Test
    void getByIsbn_checkableNotPresent() {
        String unknownIsbn = "unknown-isbn";
        when(checkableRepository.findByIsbn(unknownIsbn)).thenReturn(Optional.empty());

        assertThrows(CheckableNotFoundException.class, () -> {
            checkableService.getByIsbn(unknownIsbn);
        }, "No Checkable found with isbn: " + unknownIsbn);
    }
    @Test
    void getByIsbn() {
        Checkable expected = checkables.get(1);
        when(checkableRepository.findByIsbn("1-1")).thenReturn(Optional.ofNullable(checkables.get(1)));

        Checkable actual = checkableService.getByIsbn("1-1");
        assertEquals(expected, actual);
    }
    @Test
    void getByType_checkableNotPresent() {
        Class<Media> mediaClass = Media.class;
        when(checkableRepository.findByType(mediaClass)).thenReturn(Optional.empty());

        assertThrows(CheckableNotFoundException.class, () -> {
            checkableService.getByType(mediaClass);
        }, "No Checkable found of type: " + Media.class);
    }
    @Test
    void getByType() {
        Checkable expected = checkables.get(0);
        when(checkableRepository.findByType(Media.class)).thenReturn(Optional.ofNullable(checkables.get(0)));

        Checkable actual = checkableService.getByType(Media.class);
        assertEquals(expected, actual);
    }
    @Test
    void save() {
        Checkable testCheckable = checkables.get(0);
        checkableRepository.save(testCheckable);

        verify(checkableRepository).save(testCheckable);
    }
    @Test
    void save_checkableIsAlreadyPresent() {
        Checkable testCheckable = checkables.get(0);
        when(checkableRepository.findAll()).thenReturn(checkables);
        assertThrows(ResourceExistsException.class, () -> {
            checkableService.save(testCheckable);
        }, "Checkable with isbn: " + testCheckable.getIsbn() + " already exists!");
    }
}