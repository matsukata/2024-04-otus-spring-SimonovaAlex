package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.BookService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @ShellMethod(value = "Find all books", key = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bbid")
    public String findBookById(UUID id) {
        return bookService.findById(id)
                .map(bookConverter::bookToString)
                .orElse("Book with id %s not found".formatted(id.toString()));
    }

    // выгрузить авторов и жанры, чтобы увидеть айдишники
    @ShellMethod(value = "Insert book", key = "bins")
    public String insertBook(String title, UUID authorId, Set<UUID> genresIds) {
        var savedBook = bookService.insert(title, authorId, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    // сначала нужно выгрузить все, потом взять айдишники и удалить
    @ShellMethod(value = "Update book", key = "bupd")
    public String updateBook(UUID id, String title, UUID authorId, Set<UUID> genresIds) {
        var savedBook = bookService.update(id, title, authorId, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    // сначала нужно выгрузить все, потом взять айдишник и удалить
    @ShellMethod(value = "Delete book by id", key = "bdel")
    public void deleteBook(UUID id) {
        bookService.deleteById(id);
    }
}
