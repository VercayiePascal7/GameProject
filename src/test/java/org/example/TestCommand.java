package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCommand {

    @Test
    public void testCommandExecute() {
        final boolean[] executed = {false};

        Command command = new Command() {
            @Override
            public void execute() {
                executed[0] = true;
            }
        };

        command.execute();

        assertTrue(executed[0]);
    }
}