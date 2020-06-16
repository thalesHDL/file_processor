package org.example.exception;

public class FileProcessExceptionMessages {
    public static final String BASE_MESSAGE_EXCEPTION_INVALID_ENTITY_CLASS = "A entidade: #E, não pode ser processada pois não possui um construtor válido";
    public static final String BASE_MESSAGE_EXCEPTION_NONSTANDARD_FILE = "Arquivo: #F fora do padrão!";
    public static final String BASE_MESSAGE_EXCEPTION_INCOMPLETE_FILE = "Arquivo: #F incompleto!";
    public static final String BASE_MESSAGE_EXCEPTION_INVALID_FILE = "Arquivo: #F inválido!";

    public static final String BASE_MESSAGE_ERROR_INCOMPATIBLE_TYPES = "Error at file: #F, line: #L, entity: #E, (Incompatible types, expected: #T, and received value: #V)";
    public static final String BASE_MESSAGE_ERROR_INCORRECT_FILE_FORMAT = "Error at file: #F, line: #L, entity: #E, (Incorrect file format)";
}
