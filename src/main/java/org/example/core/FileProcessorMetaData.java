package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
class FileProcessorMetaData {
    private String fileName;
    private String entityName;
    private Integer lineCount;
    private List<String> logErros;
}
