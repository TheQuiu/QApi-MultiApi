package com.quiu.qapi.commands.errors;

public class CommandFailException extends RuntimeException
{
    public CommandFailException(String message)
    {
        super(message);
    }
}