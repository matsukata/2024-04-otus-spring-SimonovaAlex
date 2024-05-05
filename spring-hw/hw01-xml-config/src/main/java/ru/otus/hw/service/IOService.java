package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;

import java.util.List;

public interface IOService {
    void printLine(String s);

    void printFormattedLine(String s, Object... args);

    void printNumberedAnswers(Answer answer, List<Answer> answers);
}
