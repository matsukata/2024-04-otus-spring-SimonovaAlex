package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.query("select * from authors", new JdbcAuthorRepository.AuthorRowMapper());

    }

    @Override
    public Optional<Author> findById(long id) {
        Author author = (Author) namedParameterJdbcOperations.query(
                "select authors.id as id, authors.full_name as full_name from authors where id = :id",
                new MapSqlParameterSource().addValue("id", id), new AuthorRowMapper()
        );
        return Optional.of(author);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            String id = rs.getString("id");
            String name = rs.getString("full_name");
            return new Author(Long.parseLong(id), name);
        }
    }
}
    

