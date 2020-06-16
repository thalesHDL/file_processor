package models;

import lombok.Getter;
import lombok.Setter;
import org.example.core.FileProcessorIgnore;

@Getter
@Setter
public class Carro {
    private String tipo;
    private String fabricante;
    private String modelo;
    private Double preco;
    @FileProcessorIgnore
    private boolean promocao;
}
