package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.Checkable;
import com.bloomtech.library.models.checkableTypes.Media;
import com.bloomtech.library.repositories.LibraryRepository;
import com.bloomtech.library.models.CheckableAmount;
import com.bloomtech.library.views.LibraryAvailableCheckouts;
import com.bloomtech.library.views.OverdueCheckout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    //TODO: Implement behavior described by the unit tests in tst.com.bloomtech.library.services.LibraryService

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private CheckableService checkableService;



    public void save(Library library) {
        List<Library> libraries = libraryRepository.findAll();
        if (libraries.stream().filter(p->p.getName().equals(library.getName())).findFirst().isPresent()) {
            throw new ResourceExistsException("Library with name: " + library.getName() + " already exists!");
        }
        libraryRepository.save(library);
    }
    public List<Library> getLibraries() {
        return libraryRepository.findAll();
    }

    public Library getLibraryByName(String name) {

        return libraryRepository.findByName(name).orElseThrow(() -> new LibraryNotFoundException("Non-Existent Library"));
    }
    public CheckableAmount getCheckableAmount(String libraryName, String checkableIsbn) {
        Checkable checkable = checkableService.getByIsbn(checkableIsbn);
        Library library = getLibraryByName(libraryName);
        for (CheckableAmount amount : library.getCheckables()) {
            if (amount.getCheckable() == checkable) {
                return amount;
            }
        }

        return new CheckableAmount(checkable, 0);
    }
    public List<OverdueCheckout> getOverdueCheckouts(String libraryName) {
        List<OverdueCheckout> overdueCheckouts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        Library library = getLibraryByName(libraryName);
        for (LibraryCard libraryCard : library.getLibraryCards()) {
            for (Checkout checkout : libraryCard.getCheckouts()) {
                if (checkout.getDueDate().isBefore(now)) {
                    overdueCheckouts.add(new OverdueCheckout(libraryCard.getPatron(), checkout));
                }
            }
        }

        return overdueCheckouts;
    }
    public List<LibraryAvailableCheckouts> getLibrariesWithAvailableCheckout(String isbn) {
        Checkable checkable = checkableService.getByIsbn(isbn);
        List<Library> libraries = getLibraries();

        List<LibraryAvailableCheckouts> available = new ArrayList<>();
        librariesLoop:
        for (Library library : libraries) {
            for (CheckableAmount amount : library.getCheckables()) {
                if (amount.getCheckable() == checkable) {
                    available.add(new LibraryAvailableCheckouts(amount.getAmount(), library.getName()));
                    continue librariesLoop;
                }
            }
        }

        return available;
    }


}
