package ru.otus.hw.services;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BookService {
    Optional<Book> findById(UUID id);

    List<Book> findAll();

    Book insert(String title, UUID authorId, Set<UUID> genresIds);

    Book update(UUID id, String title, UUID authorId, Set<UUID> genresIds);

    void deleteById(UUID id);
}
