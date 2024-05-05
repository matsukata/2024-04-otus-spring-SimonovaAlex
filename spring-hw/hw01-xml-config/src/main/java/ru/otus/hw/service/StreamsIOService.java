package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;

import java.io.PrintStream;
import java.util.List;

public class StreamsIOService implements IOService {
    public static final int INCREMENT = 1;

    private final PrintStream printStream;

    public StreamsIOService(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    public void printNumberedAnswers(Answer answer, List<Answer> answers) {
        printStream.printf("%d. %s%n", answers.indexOf(answer) + INCREMENT, answer.text());
    }
}
