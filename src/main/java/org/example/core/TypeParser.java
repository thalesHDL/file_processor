package org.example.core;

import org.apache.commons.lang3.time.DateUtils;
import org.example.util.FileProcessorConstants;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


class TypeParser {

    private static final Map<Class, Function<String, ?>> parses = new HashMap<>();

    static {
        parses.put(Long.class, Long::parseLong);
        parses.put(Integer.class, Integer::parseInt);
        parses.put(String.class, String::toString);
        parses.put(Double.class, Double::parseDouble);
        parses.put(Float.class, Float::parseFloat);
        parses.put(Date.class, TypeParser::parseDate);
        parses.put(Boolean.class, Boolean::valueOf);
        parses.put(BigDecimal.class, BigDecimal::new);
    }

    /**
     * Função responsável por realizar a transformação de uma string em qualquer outro tipo de dado
     *
     * @param targetType: tipo em que a string será transformada
     * @param value: valor que serpa transformado
     *
     * @return Object, contento o value já transformado no targetType
     * @throws InvocationTargetException, verifica se o tipo field existe no map utilizado para fazer o parser. Caso este erro
     * apareça, basta implementar no parser o tipo que ele tentou processar e que não existe la
     */
    Object parse(Class targetType, String value) throws InvocationTargetException {
        return value != null ? parses.get(targetType).apply(value) : null;
    }

    /**
     * Método responsável por transformar uma string em Date
     *
     * @param value: valor da data no formato string
     *
     * @return Date, contendo a data presente no value
     */
    private static Date parseDate(String value) {
        try {
            return DateUtils.parseDate(value, FileProcessorConstants.DATE_FORMATES);
        } catch (ParseException e) {
            return null;
        }
    }
}
