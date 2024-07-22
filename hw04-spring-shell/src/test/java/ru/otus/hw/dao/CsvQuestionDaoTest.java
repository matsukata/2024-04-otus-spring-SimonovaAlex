package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.TestRunnerServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CsvQuestionDaoTest {
    @MockBean
    private TestRunnerServiceImpl testRunnerService;

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @Test
    void findAll() {
        assertThat(csvQuestionDao.findAll().size()).isEqualTo(3);

        List<Question> questions = getQuestionsUtil();
        assertThat(csvQuestionDao.findAll()).isEqualTo(questions);
    }

    private List<Question> getQuestionsUtil() {
        Answer answer1a = new Answer("No, not Egypt. She was actually Greek!", true);
        Answer answer1b = new Answer("From Egypt", false);
        Answer answer1c = new Answer("Who is she?", false);
        Question question1 = new Question("Where was Cleopatra from?", List.of(answer1a, answer1b, answer1c));
        Answer answer2a = new Answer("Ears", true);
        Answer answer2b = new Answer("Tongue", false);
        Answer answer2c = new Answer("Skin?", false);
        Question question2 = new Question("What is the part of our body that never stops growing?",
                List.of(answer2a, answer2b, answer2c));
        Answer answer3a = new Answer("It's impossible", false);
        Answer answer3b = new Answer("broccoli?", false);
        Answer answer3c = new Answer("tomatoes", true);
        Answer answer3d = new Answer("my favourite cucumber", false);
        Question question3 = new Question("Which vegetable has more genes than humans?",
                List.of(answer3a, answer3b, answer3c, answer3d));
        return List.of(question1, question2, question3);
    }
}