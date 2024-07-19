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
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private static final int NOT_UPDATE_COUNT = 0;

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
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

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
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
                        new BookGenreRowMapper()
                );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        for (Book book : booksWithoutGenres) {
            List<Long> genreIds = relations.stream()
                    .filter(bookGenreRelation -> bookGenreRelation.bookId() == (book.getId()))
                    .map(rel -> rel.genreId).toList();
            List<Genre> genreList = genres.stream()
                    .filter(genre -> genreIds.contains(genre.getId()))
                    .collect(Collectors.toList());
            book.setGenres(genreList);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());

        namedParameterJdbcOperations.update(
                "insert into books (title, author_id) values(:title,:author_id)",
                params,
                keyHolder
        );

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("bookId", book.getId())
                .addValue("bookTitle", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());
        int updated = namedParameterJdbcOperations.update(
                "update books set title=:bookTitle, author_id=:authorId where id=:bookId",
                mapSqlParameterSource
        );
        if (updated == NOT_UPDATE_COUNT) {
            throw new EntityNotFoundException("Entity not found");
        }

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
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
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = Long.parseLong(rs.getString("id"));
            String title = rs.getString("title");
            long authorId = Long.parseLong(rs.getString("author"));
            String fullName = rs.getString("full_name");
            List<Genre> genres = new ArrayList<>();
            return new Book(id, title, new Author(authorId, fullName), genres);
        }
    }

    private static class BookGenreRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int i) throws SQLException {
            long bookId = Long.parseLong(rs.getString("book_id"));
            long genreId = Long.parseLong(rs.getString("genre_id"));
            return new BookGenreRelation(bookId, genreId);
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws DataAccessException {
            try {
                Book book = null;
                if (!rs.isBeforeFirst()) {
                    return book;
                }
                List<Genre> genres = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString("bookId");
                    String authorId = rs.getString("author");
                    String title = rs.getString("title");
                    String fullName = rs.getString("fullName");
                    book = Book.builder().id(Long.parseLong(id))
                            .author(new Author(Long.parseLong(authorId), fullName)).title(title).build();
                    String genreId = rs.getString("genreId");
                    String genreName = rs.getString("genreName");
                    genres.add(new Genre(Long.parseLong(genreId), genreName));
                }
                assert book != null;
                book.setGenres(genres);
                return book;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}