package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;

import javax.security.auth.login.LoginContext;

@ShellComponent(value = "Application Commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    private final LoginContext loginContext;

    @ShellMethod(value = "Login command", key = {"l", "login"})
    public String login(@ShellOption(defaultValue = "AnyUser") String userName) {
        loginContext.login(userName);
        return String.format("Добро пожаловать: %s", userName);
    }

    @ShellMethod(value = "Login command", key = {"login"})
    @ShellMethodAvailability(value = "isLoginCommandAvailable")
    public String publishEvent() {
        eventsPublisher.publish();
        return "Событие опубликовано";
    }

    private Availability isPublishEventCommandAvailable() {
        return loginContext.isUserLoggedIn()
                ? Availability.available()
                : Availability.unavailable("Сначала залогиньтесь");
    }
}
