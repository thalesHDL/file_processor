package org.example.core;

import org.example.exception.FileProcessExceptionMessages;
import org.example.util.FileProcessorConstants;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface FileProcessorBase {
    default List<String> buildMessageErrorConstraintViolation(Set<ConstraintViolation<Object>> constraintsViolations) {
        List<String> listErros = new ArrayList<>();
        for (ConstraintViolation<Object> constraint : constraintsViolations) {
            listErros.add(buildMessageErrorConstraintViolation(constraint));
        }
        return listErros;
    }

    default String buildMessageExceptionNonstandardFile(String fileName) {
        return FileProcessExceptionMessages.BASE_MESSAGE_EXCEPTION_NONSTANDARD_FILE
            .replace("#F", fileName);
    }

    default String buildMessageExceptionIncompleteFile(String fileName) {
        return FileProcessExceptionMessages.BASE_MESSAGE_EXCEPTION_INCOMPLETE_FILE
            .replace("#F", fileName);
    }

    default String buildMessageExceptionInvalidFile(String fileName) {
        return FileProcessExceptionMessages.BASE_MESSAGE_EXCEPTION_INVALID_FILE
            .replace("#F", fileName);
    }

    default String buildMessageExceptionInvalidEntityClass(String entityName) {
        return FileProcessExceptionMessages.BASE_MESSAGE_EXCEPTION_INVALID_ENTITY_CLASS
            .replace("#E", entityName);
    }

    default String buildMessageErrorIncompatibleTypes(FileProcessorMetaData metaData, String columnValue, String fieldType) {
        return FileProcessExceptionMessages.BASE_MESSAGE_ERROR_INCOMPATIBLE_TYPES
            .replace("#F", metaData.getFileName())
            .replace("#L", metaData.getLineCount().toString())
            .replace("#E", metaData.getEntityName())
            .replace("#T", fieldType)
            .replace("#V", columnValue);
    }

    default String buildMessageErrorIncorrectFileFormat(FileProcessorMetaData metaData) {
        return FileProcessExceptionMessages.BASE_MESSAGE_ERROR_INCORRECT_FILE_FORMAT
            .replace("#F", metaData.getFileName())
            .replace("#L", metaData.getLineCount().toString())
            .replace("#E", metaData.getEntityName());
    }

    default String buildMessageErrorConstraintViolation(ConstraintViolation<Object> constraint) {
        return "Erro ao processar a entidade: " +
            constraint.getRootBeanClass().getName() +
            ". Valor do atributo: " +
            constraint.getPropertyPath() +
            " não respeita a constraint: " +
            constraint.getMessage() +
            " (value = " +
            constraint.getInvalidValue() +
            ")";
    }
}
