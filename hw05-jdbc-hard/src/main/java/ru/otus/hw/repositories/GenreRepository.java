package ru.otus.hw.repositories;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findAllByIds(Set<UUID> ids);
}
