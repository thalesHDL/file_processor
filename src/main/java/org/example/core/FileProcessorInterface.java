package org.example.core;

import org.example.util.FileP;

import java.io.IOException;
import java.util.List;

interface FileProcessorInterface<LINE_TYPE, COL_TYPE> {
    /**
     * Método responsável por trasformar o conteudo do arquivo em uma lista de linhas do tipo LINE_TYPE
     *
     * @param file: parâmetro que contém o conteúdo do arquivo a ser trasformado
     *
     * @return List<LINE_TYPE>, na qual cada elemento corresponde à uma linha do arquivo
     *
     * @throws IOException: verificação para casos de arquivos corrompidos
     */
    List<LINE_TYPE> getLinesFromFileAsList(FileP file) throws IOException;

    /**
     * Método responsável por trasformar uma linha em uma lista de colunas do tipo COL_TYPE
     *
     * @param line: corresponde à uma linha do arquivo
     *
     * @return List<COL_TYPE>, na qual cada elemento corresponde a uma coluna presente na linha
     */
    List<COL_TYPE> getColsFromLineAsList(LINE_TYPE line);

    /**
     * Método responsável por transformar o conteúdo de uma coluna do tipo COL_TYPE em string
     *
     * @param column: valor da coluna
     *
     * @return String, retorna o valor da coluna trasformado em string
     */
    String getValueFromColumnAsString(COL_TYPE column);
}
