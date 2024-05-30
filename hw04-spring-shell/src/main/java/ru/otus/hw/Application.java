package ru.otus.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.shell.command.annotation.CommandScan;
import ru.otus.hw.service.TestRunnerService;

@CommandScan
public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        ApplicationContext context = null;
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}