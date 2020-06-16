package org.example.processors;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.core.FileProcessor;
import org.example.util.FileP;
import org.example.util.FileProcessorConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class XlsxFileProcessor extends FileProcessor<Row, Cell> {

    /**
     * Método responsável por trasformar o conteudo do arquivo csv em uma lista de linhas
     *
     * @param file: parâmetro que contém o conteúdo do arquivo a ser trasformado
     *
     * @return List<Row>, na qual cada elemento corresponde à uma linha do arquivo
     *
     * @throws IOException: verificação para casos de arquivos corrompidos
     */
    @Override
    public List<Row> getLinesFromFileAsList(FileP file) throws IOException {
        XSSFWorkbook content = new XSSFWorkbook(new ByteArrayInputStream(file.getContent()));
        Iterator<Row> rowIterator = content.getSheetAt(0).iterator();
        content.close();
        return IteratorUtils.toList(rowIterator);
    }

    /**
     * Método responsável por trasformar uma linha em uma lista de colunas
     *
     * @param line: corresponde à uma linha do arquivo
     *
     * @return List<Cell>, na qual cada elemento corresponde a uma coluna presente na linha
     */
    @Override
    public List<Cell> getColsFromLineAsList(Row line) {
        return IteratorUtils.toList(line.cellIterator());
    }

    /**
     * Método responsável por transformar o conteúdo de uma coluna em string
     *
     * @param column: valor da coluna
     *
     * @return String, retorna o valor da coluna trasformado em string
     */
    @Override
    public String getValueFromColumnAsString(Cell column) {
        switch (column.getCellTypeEnum()) {
            case STRING:
                return String.valueOf(column.getStringCellValue()).trim();
            case BOOLEAN:
                return String.valueOf(column.getBooleanCellValue()).trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(column)) {
                    return new DataFormatter().formatCellValue(column).trim();
                } else {
                    return BigDecimal.valueOf(column.getNumericCellValue()).setScale(FileProcessorConstants.INDEX_CELULA_INICIAL_XLSX).toPlainString();
                }
            case FORMULA:
                return String.valueOf(column.getCellFormula()).trim();
            default:
                return null;
        }
    }
}
