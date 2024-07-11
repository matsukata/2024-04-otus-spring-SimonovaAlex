package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.query("select * from authors", new JdbcAuthorRepository.AuthorRowMapper());

    }

    public Optional<Author> findByFullName(String fullName) {
        Map<String, Object> params = Collections.singletonMap("fullName", fullName);
        Author author = namedParameterJdbcOperations
                .queryForObject(
                        "select id, full_name from authors where authors.full_name = :fullName",
                        params,
                        new JdbcAuthorRepository.AuthorRowMapper()
                );
        return author == null ? Optional.empty() : Optional.of(author);
    }

    @Override
    public Optional<Author> findById(UUID id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        Author author = namedParameterJdbcOperations.queryForObject(
                "select id, full_name from authors where id = :id", params, new AuthorRowMapper()
        );
        return author == null ? Optional.empty() : Optional.of(author);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String name = resultSet.getString("full_name");
            return new Author(id, name);
        }
    }
}
    

