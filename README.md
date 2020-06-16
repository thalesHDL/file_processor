# file_processor

Este projeto se trata de uma forma de percorrer os arquivos e realizar o parse dos dados do arquivo para uma lista de um determinado tipo.

No projeto tem um exemplo na parte de testes, onde há uma classe Carro e um arquivo contendo os dados para preencher os campos da classe.

```
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
```

```
"Base de dados para exemplo" 
"Esta base contem informações sobre carros"  
Tipo,Fabricante,Modelo,Preço 
Flex,Chevrolet,NOVO ONIX,53050
Flex,Chevrolet,JOY,52150 
Flex,Chevrolet,SPIN,75150 
```

Como é possível observar, entre os dados não há uma coluna para promoção, e por isto este campo é anotato com `@FileProcessorIgnore`.
