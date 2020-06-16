package org.example.processors;

import org.example.core.FileProcessor;
import org.example.util.FileP;
import org.example.util.FileProcessorConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

;

public class CsvFileProcessor extends FileProcessor<String, String> {

    /**
     * Método responsável por trasformar o conteudo do arquivo csv em uma lista de linhas
     *
     * @param file: parâmetro que contém o conteúdo do arquivo a ser trasformado
     *
     * @return List<String>, na qual cada elemento corresponde à uma linha do arquivo
     *
     * @throws IOException: verificação para casos de arquivos corrompidos
     */
    @Override
    public List<String> getLinesFromFileAsList(FileP file) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file.getContent()), StandardCharsets.UTF_8));
        return buffer.lines().collect(Collectors.toList());
    }

    /**
     * Método responsável por trasformar uma linha em uma lista de colunas
     *
     * @param line: corresponde à uma linha do arquivo
     *
     * @return List<String>, na qual cada elemento corresponde a uma coluna presente na linha
     */
    @Override
    public List<String> getColsFromLineAsList(String line) {
        return Arrays.asList(line.split(FileProcessorConstants.REGEX_IGNORA_VIRG_ASPAS, -1));
    }

    /**
     * Método responsável por transformar o conteúdo de uma coluna em string
     *
     * @param column: valor da coluna
     *
     * @return String, retorna o valor da coluna trasformado em string
     */
    @Override
    public String getValueFromColumnAsString(String column) {
        return column;
    }
}
