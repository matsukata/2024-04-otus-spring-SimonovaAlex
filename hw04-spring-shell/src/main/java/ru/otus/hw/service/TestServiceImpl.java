package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {


    private static final int MIN = 1;

    private static final int INCREMENT = 1;

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {

        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        try {
            for (var question : questions) {
                var isAnswerValid = false;
                ioService.printLine(question.text());
                ioService.printLine("");
                List<Answer> answers = question.answers();
                printNumberedAnswers(answers);
                int number = ioService.readIntForRangeLocalized(MIN, question.answers().size(), "TestService.error");
                Answer answer = answers.get(--number);
                isAnswerValid = answer.isCorrect();
                testResult.applyAnswer(question, isAnswerValid);
            }
        } catch (IllegalArgumentException e) {
            ioService.printLineLocalized("TestService.cheer.up");
        }
        return testResult;
    }

    private void printNumberedAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%d. %s%n", i + INCREMENT, answers.get(i).text());
        }
    }
}
