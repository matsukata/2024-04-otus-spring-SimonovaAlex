package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcOperations.query(
                "select * from genres",
                new JdbcGenreRepository.GenreRowMapper()
        );
    }

    public Optional<Genre> findByFullName(String name) {
        Map<String, Object> params = Collections.singletonMap("name", name);
        Genre genre = namedParameterJdbcOperations.queryForObject(
                "select id, name from genres where genres.name = :name",
                params,
                new JdbcGenreRepository.GenreRowMapper()
        );
        return genre == null ? Optional.empty() : Optional.of(genre);
    }

    @Override
    public List<Genre> findAllByIds(Set<UUID> ids) {
        Map<String, Object> params = Collections.singletonMap("ids", ids);
        return namedParameterJdbcOperations.query(
                "select * from genres where id in (:ids)",
                params,
                new JdbcGenreRepository.GenreRowMapper()
        );
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            UUID id = UUID.fromString(rs.getString("id"));
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
