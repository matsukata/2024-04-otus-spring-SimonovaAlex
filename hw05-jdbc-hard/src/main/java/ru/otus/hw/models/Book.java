package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private long id;

    private String title;

    private Author author;

    private List<Genre> genres;
}
