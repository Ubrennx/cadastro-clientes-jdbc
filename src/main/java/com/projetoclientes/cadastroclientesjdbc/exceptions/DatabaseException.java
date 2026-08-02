package com.projetoclientes.cadastroclientesjdbc.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
