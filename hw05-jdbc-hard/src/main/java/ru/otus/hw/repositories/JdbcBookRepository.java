package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private static final int NOT_UPDATE_COUNT = 0;

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(UUID id) {
        Book book = namedParameterJdbcOperations.query("select " +
                "books.id as bookId," +
                "books.author_id as author," +
                "books.title as title," +
                "authors.full_name as fullName, " +
                "genres.id as genreId, " +
                "genres.name as genreName " +
                "from books  left join authors on " +
                "books.author_id = authors.id " +
                "inner join books_genres on books.id = books_genres.book_id " +
                "inner join genres on genres.id = books_genres.genre_id " +
                "where books.id = :id", new MapSqlParameterSource().addValue("id", id), new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    public Optional<Book> findByTitle(String title) {
        Map<String, Object> params = Collections.singletonMap("title", title);
        Book book = namedParameterJdbcOperations.queryForObject("select " +
                "books.id as id," +
                " books.author_id as author," +
                " books.title as title," +
                " authors.full_name as full_name" +
                " from books  left join authors on " +
                "books.author_id = authors.id " +
                "where books.title = :title ", params, new JdbcBookRepository.BookRowMapper());
        return book == null ? Optional.empty() : Optional.of(book);
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(UUID id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :id", params);
        namedParameterJdbcOperations.update(
                "delete from books where id = :id", params
        );
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query("select " +
                "books.id as id," +
                " books.author_id as author," +
                " books.title as title," +
                " authors.full_name as full_name" +
                " from books  left join authors on " +
                "books.author_id = authors.id " +
                "group by books.id ", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations
                .query(
                        "select * from books_genres",
                        new JdbcBookRepository.BookGenreRowMapper()
                );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        for (Book book : booksWithoutGenres) {
            List<UUID> genreIds = relations.stream()
                    .filter(bookGenreRelation -> bookGenreRelation.bookId().equals(book.getId()))
                    .map(rel -> rel.genreId).toList();
            List<Genre> genreList = genres.stream()
                    .filter(genre -> genreIds.contains(genre.getId()))
                    .collect(Collectors.toList());
            book.setGenres(genreList);
        }
    }

    private Book insert(Book book) {
        //TODO как генерировать айдишник
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", UUID.randomUUID());
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcOperations.update(
                "insert into books (id,title, author_id) values(:id,:title,:author_id)",
                params,
                keyHolder
        );
        book.setId(keyHolder.getKeyAs(UUID.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("bookId", book.getId().toString())
                .addValue("bookTitle", book.getTitle())
                .addValue("authorId", book.getAuthor().getId().toString());
        int updated = namedParameterJdbcOperations.update(
                "update books set title=:bookTitle where id=:bookId",
                mapSqlParameterSource
        );
        if (updated == NOT_UPDATE_COUNT) {
            throw new EntityNotFoundException("Entity not found");
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {

        List<BookGenreRelation> bookGenreRelations = book.getGenres()
                .stream()
                .map(genre -> new BookGenreRelation(book.getId(), genre.getId()))
                .toList();

        namedParameterJdbcOperations.batchUpdate(
                "insert into books_genres (book_id, genre_id) values (:bookId, :genreId)",
                SqlParameterSourceUtils.createBatch(bookGenreRelations)
        );
    }

    private void removeGenresRelationsFor(Book book) {
        Map<String, Object> params = Collections.singletonMap("id", book.getId());
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :id", params);
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String title = resultSet.getString("title");
            String authorId = resultSet.getString("author");
            String fullName = resultSet.getString("full_name");
            List<Genre> genres = new ArrayList<>();
            return new Book(id, title, new Author(UUID.fromString(authorId), fullName), genres);
        }
    }

    private static class BookGenreRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int i) throws SQLException {
            UUID bookId = UUID.fromString(rs.getString("book_id"));
            UUID genreId = UUID.fromString(rs.getString("genre_id"));
            return new BookGenreRelation(bookId, genreId);
        }
    }

    // Использовать для findById
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @SuppressWarnings("checkstyle:MethodLength")
        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            try {
                List<Book> list = new ArrayList<>();
                List<Genre> genres = new ArrayList<>();
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("bookId"));
                    UUID authorId = UUID.fromString(rs.getString("author"));
                    String title = rs.getString("title");
                    String fullName = rs.getString("fullName");

                    list.add(Book.builder()
                            .id(id)
                            .author(new Author(authorId, fullName))
                            .title(title)
                            .build());

                    String genreId = rs.getString("genreId");
                    String genreName = rs.getString("genreName");

                    genres.add(new Genre(UUID.fromString(genreId), genreName));
                }
                if (list.isEmpty()) {
                    throw new EntityNotFoundException("Book is not found");
                }
                Book book = list.iterator().next();
                book.setGenres(genres);
                return book;
            } catch (EntityNotFoundException ex) {
                //Здесь логгирование
                return null;
            }
        }
    }

    private record BookGenreRelation(UUID bookId, UUID genreId) {
    }
}
