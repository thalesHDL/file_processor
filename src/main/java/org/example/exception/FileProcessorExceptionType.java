package org.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileProcessorExceptionType {

    INFALID_FILE("arquivo.invalido"),
    NONSTANDARD_FILE("arquivo.foraPadrao"),
    INCOMPLETE_FILE("entidade.incompleto"),
    INVALID_ENTITY_CLASS("entidade.invalida");

    private String errorCode;
}
