package com.martingago.words.exceptions;

public class CustomExceptions {

    public static class FileEmptyException extends RuntimeException {
        public FileEmptyException(String message) {
            super(message);
        }
    }

    public static class UnsupportedFileTypeException extends RuntimeException {
        public UnsupportedFileTypeException(String message) {
            super(message);
        }
    }

    public static class WordLimitExceededException extends RuntimeException {
        public WordLimitExceededException(String message) {
            super(message);
        }
    }

    public static class NoValidWordsException extends RuntimeException {
        public NoValidWordsException(String message) {
            super(message);
        }
    }

}
