package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Author> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        Author author = namedParameterJdbcOperations.queryForObject(
                "select id, full_name from authors where id = :id", params, new AuthorRowMapper()
        );
        return author == null ? Optional.empty() : Optional.of(author);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

            @Override
            public Author mapRow(ResultSet resultSet, int i) throws SQLException {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("full_name");
                return new Author(id, name);
            }
        }
    }
    
}
