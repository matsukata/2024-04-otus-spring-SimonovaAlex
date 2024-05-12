package ru.otus.hw.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class CsvQuestionDaoTest {

    @Mock
    private AppProperties fileNameProviderMock;

    @Test
    void findAll() {
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(fileNameProviderMock);

        Mockito.when(fileNameProviderMock.getTestFileName()).thenReturn("test-questions.csv");
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
        Question question2 = new Question("What is the part of our body that never stops growing?", List.of(answer2a, answer2b, answer2c));
        Answer answer3a = new Answer("It's impossible", false);
        Answer answer3b = new Answer("broccoli?", false);
        Answer answer3c = new Answer("tomatoes", true);
        Answer answer3d = new Answer("my favourite cucumber", false);
        Question question3 = new Question("Which vegetable has more genes than humans?", List.of(answer3a, answer3b, answer3c, answer3d));
        return List.of(question1, question2, question3);
    }
}