package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    public static final int INCREMENT = 1;

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();

        questions.forEach(question -> {
            ioService.printLine(question.text());
            ioService.printLine("");
            List<Answer> answers = question.answers();
            printNumberedAnswers(answers);
        });
    }

    private void printNumberedAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%d. %s%n", i + INCREMENT, answers.get(i).text());
        }
    }
}
