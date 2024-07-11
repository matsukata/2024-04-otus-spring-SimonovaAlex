package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    Optional<Book> findById(UUID id);

    List<Book> findAll();

    Book save(Book book);

    void deleteById(UUID id);
}
