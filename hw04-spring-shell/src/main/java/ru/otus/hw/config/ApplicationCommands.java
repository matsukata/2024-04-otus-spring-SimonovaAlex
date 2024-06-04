package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.security.LoginContext;
import ru.otus.hw.service.LocalizedIOServiceImpl;
import ru.otus.hw.service.TestRunnerService;


@ShellComponent(value = "Application Commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    private final TestRunnerService testRunnerService;

    private final LoginContext loginContext;

    private final LocalizedIOServiceImpl localizedIOService;


    @ShellMethod(value = "Login command", key = {"l", "login"})
    public void login(@ShellOption(defaultValue = "AnyUser") String userName) {
        loginContext.login(userName);
        localizedIOService.printFormattedLineLocalized("ApplicationCommands.welcome", userName);
    }

    @ShellMethod(value = "Start testing", key = {"t", "test"})
    @ShellMethodAvailability(value = "isTestCommandAvailable")
    public void startTesting() {
        testRunnerService.run();
    }

    private Availability isTestCommandAvailable() {
        return loginContext.isUserLoggedIn()
                ? Availability.available()
                : Availability.unavailable(localizedIOService.getMessage("ApplicationCommands.login"));
    }

}
