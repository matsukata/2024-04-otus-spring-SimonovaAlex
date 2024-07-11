package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
@Import({JdbcBookRepository.class, JdbcGenreRepository.class, JdbcAuthorRepository.class})
class JdbcBookRepositoryTest {

    @Autowired
    private JdbcBookRepository repositoryJdbc;

    @Autowired
    private JdbcAuthorRepository authorRepository;

    @Autowired
    private JdbcGenreRepository jdbcGenreRepository;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Book bookExp = repositoryJdbc.findAll().stream()
                .filter(book -> book.getTitle().equals("Title_1"))
                .findFirst()
                .orElseThrow();
        Book actualBook = repositoryJdbc.findById(bookExp.getId()).orElseThrow();
        assertThat(actualBook)
                .usingComparatorForFields((x, y) -> 0, "id", "title", "author")
                .usingRecursiveComparison().ignoringFields("genres")
                .isEqualTo(bookExp);
        assertThat(actualBook.getGenres()).containsExactlyInAnyOrderElementsOf(bookExp.getGenres());
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repositoryJdbc.findAll();
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFieldsOfTypes(UUID.class)
                .isEqualTo(dbBooks);
        assertThat(actualBooks).isNotNull().hasSize(3);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author authorFoundByName = authorRepository.findByFullName("Author_1").orElseThrow();
        Genre genre1 = jdbcGenreRepository.findByFullName("Genre_1").orElseThrow();
        Genre genre2 = jdbcGenreRepository.findByFullName("Genre_2").orElseThrow();
        var expectedBook = new Book(null, "BookTitle_10500", authorFoundByName, List.of(genre1, genre2));
        var savedBook = repositoryJdbc.save(expectedBook);

        var book = repositoryJdbc.findById(savedBook.getId()).orElseThrow();

        assertThat(expectedBook)
                .usingComparatorForFields((x, y) -> 0, "title", "author")
                .usingRecursiveComparison()
                .ignoringFields("genres", "id")
                .isEqualTo(book);
        assertThat(savedBook.getGenres()).containsExactlyInAnyOrderElementsOf(book.getGenres());
        assertEquals(book.getTitle(), expectedBook.getTitle());
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Book book = repositoryJdbc.findByTitle("Title_1").orElseThrow();
        Author authorFoundByName = authorRepository.findByFullName("Author_1").orElseThrow();
        Genre genre1 = jdbcGenreRepository.findByFullName("Genre_1").orElseThrow();
        Genre genre2 = jdbcGenreRepository.findByFullName("Genre_2").orElseThrow();
        var preparedBook = new Book(book.getId(), "BookTitle_10500", authorFoundByName,
                List.of(genre1, genre2));
        repositoryJdbc.save(preparedBook);
        var updatedBook = repositoryJdbc.findById(book.getId()).orElseThrow();
        assertEquals(updatedBook.getTitle(), "BookTitle_10500");
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        Book book = repositoryJdbc.findByTitle("Title_1").orElseThrow();
        assertThat(repositoryJdbc.findById(book.getId())).isPresent();
        repositoryJdbc.deleteById(book.getId());
        repositoryJdbc.findById(book.getId());
        assertThat(false);
    }

    private static List<Author> getDbAuthors() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author(UUID.randomUUID(), "Author_1"));
        authors.add(new Author(UUID.randomUUID(), "Author_2"));
        authors.add(new Author(UUID.randomUUID(), "Author_3"));
        return authors;
    }

    private static List<Genre> getDbGenres() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(UUID.randomUUID(), "Genre_1"));
        genres.add(new Genre(UUID.randomUUID(), "Genre_2"));
        genres.add(new Genre(UUID.randomUUID(), "Genre_3"));
        genres.add(new Genre(UUID.randomUUID(), "Genre_4"));
        genres.add(new Genre(UUID.randomUUID(), "Genre_5"));
        genres.add(new Genre(UUID.randomUUID(), "Genre_6"));
        return genres;
    }

    @SuppressWarnings("checkstyle:MethodLength")
    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        List<Book> books = new ArrayList<>();
        Author author1 = dbAuthors.stream()
                .filter(author -> author.getFullName().equals("Author_1"))
                .findFirst()
                .orElseThrow();
        Author author2 = dbAuthors.stream()
                .filter(author -> author.getFullName().equals("Author_2"))
                .findFirst()
                .orElseThrow();
        Author author3 = dbAuthors.stream()
                .filter(author -> author.getFullName().equals("Author_3"))
                .findFirst()
                .orElseThrow();

        Genre genre1 = dbGenres.stream()
                .filter(genre -> genre.getName().equals("Genre_1"))
                .findFirst()
                .orElseThrow();
        Genre genre2 = dbGenres.stream()
                .filter(genre -> genre.getName().equals("Genre_2"))
                .findFirst()
                .orElseThrow();
        Genre genre3 = dbGenres.stream()
                .filter(genre -> genre.getName().equals("Genre_3"))
                .findFirst()
                .orElseThrow();
        Genre genre4 = dbGenres.stream()
                .filter(genre -> genre.getName().equals("Genre_4"))
                .findFirst()
                .orElseThrow();
        Genre genre5 = dbGenres.stream()
                .filter(genre -> genre.getName().equals("Genre_5"))
                .findFirst()
                .orElseThrow();
        Genre genre6 = dbGenres
                .stream().filter(genre -> genre.getName().equals("Genre_6"))
                .findFirst()
                .orElseThrow();

        books.add(new Book(UUID.randomUUID(), "Title_1", author1, List.of(genre1, genre2)));
        books.add(new Book(UUID.randomUUID(), "Title_2", author2, List.of(genre3, genre4)));
        books.add(new Book(UUID.randomUUID(), "Title_3", author3, List.of(genre5, genre6)));
        return books;
    }

}