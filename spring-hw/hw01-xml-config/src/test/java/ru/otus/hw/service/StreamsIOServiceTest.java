package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
@Configuration
@RequiredArgsConstructor
class StreamsIOServiceTest {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/test-context.xml");


    @Test
    void printLine() {
        StreamsIOService ioService = context.getBean(StreamsIOService.class);
        ioService.printLine("Тестовая строка");
    }
}