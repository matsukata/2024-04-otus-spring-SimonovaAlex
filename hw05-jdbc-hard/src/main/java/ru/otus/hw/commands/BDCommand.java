package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Console;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.sql.SQLException;

@RequiredArgsConstructor
@ShellComponent
public class BDCommand {

    @ShellMethod(value = "start h2", key = "h2")
    public void startH2() throws SQLException {
        Console.main();
    }
}
