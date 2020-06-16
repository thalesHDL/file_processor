package org.example.util;

import org.example.exception.FileProcessException;

import java.util.List;

public interface FileProcessorStrategy {
    /**
     * Neste método deve ser implementado a chamada para o processamento do arquivo informado
     * qual a classe será usada como base para o processamento
     *
     * @param file: parâmetro que contém as informações do arquivo que será processado
     * @param classe: classe que será tomada como base para o processamento do arquivo
     *
     * @return List<?>, lista do tipo da classe base, contendo os dados presentes no arquivo
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo
     */
    List<?> process(FileP file, Class classe) throws FileProcessException;
}
