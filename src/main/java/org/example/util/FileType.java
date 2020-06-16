package org.example.util;

import org.example.exception.FileProcessException;
import org.example.processors.CsvFileProcessor;
import org.example.processors.XlsxFileProcessor;

import java.util.List;

public enum FileType implements FileProcessorStrategy {
    XLSX() {
        @Override
        public List<?> process(FileP file, Class classe) throws FileProcessException {
            return new XlsxFileProcessor().process(file, classe);
        }
    },
    CSV() {
        @Override
        public List<?> process(FileP file, Class classe) throws FileProcessException {
            return new CsvFileProcessor().process(file, classe);
        }
    };
}
