package com.example.checkdulu.exception;

public class CheckDuluException extends RuntimeException {

    public CheckDuluException(String message, Exception e){
        super(message, e);
    }

    public static class DatabaseException extends CheckDuluException {
        public DatabaseException(String message, Exception e){
            super(message, e);
        }
    }
}
