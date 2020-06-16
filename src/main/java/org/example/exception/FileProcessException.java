package org.example.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class FileProcessException extends Exception {

    private List<String> logErros;
    private FileProcessorExceptionType typeError;

    public FileProcessException(String message, List<String> logErros, FileProcessorExceptionType typeError) {
        super(message);
        this.logErros = logErros;
        this.typeError = typeError;
    }
}
