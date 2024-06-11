package ru.otus.hw.service;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@SpringBootTest
class TestServiceImplTest {

    @MockBean
    private TestRunnerServiceImpl testRunnerService;

    @MockBean
    private StreamsIOService streamsIOService;

    @Autowired
    private TestServiceImpl testService;


    @PostConstruct
    public void setProperty() {
        String input = "1";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }


    @Test
    void executeTestFor() {
        Student student = new Student("R2", "D2");
        setProperty();
        Mockito.when(streamsIOService.readIntForRange(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).thenReturn(1);
        TestResult testResult = testService.executeTestFor(student);
        Assertions.assertEquals(2, testResult.getRightAnswersCount());
    }
}