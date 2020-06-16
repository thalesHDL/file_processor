package org.example.core;

import org.example.util.FileP;
import org.example.exception.FileProcessException;
import org.example.exception.FileProcessorExceptionType;
import org.example.util.FileProcessorConstants;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Esta classe é responsável por realizar o processamento de um arquivo de acordo com
 * uma classe base informada. O que a classe faz é simplesmente percorrer as linhas do
 * arquivo e ir transformando essas linhas em uma lista do tipo da classe base informada.
 *
 * No entanto como se trata de uma uma classe abstrata é necessário que outra classe extenda
 * dela para que as funções possam ser utilizadas. Sendo assim, a classe que estender desta,
 * deverá informar qual o tipo da linha (LINE_TYPE), qual o tipo da coluna(COL_TYPE) e implementar
 * 3 métodos (getLinesFromFileAsList, getColsFromLineAsList, getValueFromColumnAsString).
 *
 * @param <LINE_TYPE>: tipo da linha presente no arquivo
 * @param <COL_TYPE>: tipo da coluna presente no arquivo
 */
public abstract class FileProcessor<LINE_TYPE, COL_TYPE> implements FileProcessorInterface<LINE_TYPE, COL_TYPE>, FileProcessorBase {

    private FileProcessorMetaData metaData;

    /**
     * Função na qual o metaData é iniciado e onde se inicia o processamento do arquivo
     *
     * @param file: parâmetro que contém as informações do arquivo que será processado
     * @param classe: classe que será tomada como base para o processamento do arquivo
     *
     * @return List<?>, lista do tipo da classe base, contendo os dados presentes no arquivo
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo
     */
    public List<?> process(FileP file, Class classe) throws FileProcessException {
        initMetaData(file, classe);
        List<Object> result = new ArrayList<>();
        List<LINE_TYPE> lines = getLines(file);
        processLines(lines, classe, result);
        return result;
    }

    /**
     * Função que remove as linhas que não contém dados a serem processados, e percorre o restante
     * realizando o processamento
     *
     * @param lines: lista do tipo LINE_TYPE que contém todas as linhas do arquivo
     * @param classe: classe que será tomada como base para o processamento do arquivo
     * @param result: váriavel onde será montado o resultado do processamento, sendo retornado ao final
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo.
     * Como neste método é feito o processamento de todas as linhas, ele também é resposável por repassar o erro que ocorreu
     * durante o processamento de cada linha. Deste modo, somente ao finalizar o processamento os erro são gerados.
     *
     */
    private void processLines(List<LINE_TYPE> lines, Class classe, List<Object> result) throws FileProcessException {
        List<Field> fields = getValidFields(classe);
        removeUnnecessaryLines(lines, fields);
        removeHeader(lines);
        boolean isLinesValids = true;
        for(int i = 0; i < lines.size(); i++) {
            updateLineMetaData();
            LINE_TYPE line = lines.get(i);
            Object entity = getEmptyInstanceOfEntity(classe);
            try {
                processLine(entity, line, fields);
                result.add(entity);
            } catch (FileProcessException e) {
                isLinesValids = false;
            }
        }
        if (!isLinesValids) {
            throw new FileProcessException(buildMessageExceptionNonstandardFile(metaData.getFileName()), metaData.getLogErros(), FileProcessorExceptionType.NONSTANDARD_FILE);
        }
    }

    /**
     * Método responsável por realizar o processamento de uma única linha do arquivo. Neste método os dados presentes
     * nesta linha serão transformados em um objeto (classe base). Para isso inicialmente é recuperada todas as colunas
     * da linha através do método getColsFromLineAsList implementado pela classe externa. Após isto cada coluna é setada
     * em seu respectivo campo no objeto processado.
     *
     * @param entity: objeto instanciado do tipo da classe base
     * @param line: linha contendo os dados que serão adicionados na entity
     * @param fields: fields válido da classe base
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo
     * É neste método onde o objeto é efetivamente formado, portando caso ocorra alguma incompatibilidade de tipo entre
     * o arquivo e o objeto, isso desencadeará um erro. No entanto o erro não será passado a frente até que toda a linha
     * seja processada.
     */
    private void processLine(Object entity, LINE_TYPE line, List<Field> fields) throws FileProcessException {
        List<COL_TYPE> columns = getColsFromLineAsList(line);
        boolean isEntityValid = true;
        for(int i = 0; i < columns.size(); i++) {
            COL_TYPE column = columns.get(i);
            Field field = fields.get(i);
            try {
                processField(field, column, entity);
            } catch (InvocationTargetException | IllegalAccessException | NumberFormatException e) {
                isEntityValid = false;
                metaData.getLogErros().add(buildMessageErrorIncompatibleTypes(metaData, getValueFromColumnAsString(column), field.getType().getName()));
            }
        }
        if (!isEntityValid) {
            throw new FileProcessException(buildMessageExceptionNonstandardFile(metaData.getFileName()), metaData.getLogErros(), FileProcessorExceptionType.NONSTANDARD_FILE);
        }
    }

    /**
     * Método responsável por realizar o processamento de uma coluna. Inicialmente é recuperado o dado da coluna através
     * da função getValueFromColumnAsString, que deverá ser implementada pela classe externa. Em seguida o valor recuperado da
     * coluna é trasformado de acordo com o tipo do field. Por fim a entity é setada com o valor transformado.
     *
     * @param field: field da classe base que será setato
     * @param column: coluna do arquivo contendo a informação utilizada para preencher a entity
     * @param entity: instancia da classe base onde será setado o dado presente na coluna
     *
     * @throws InvocationTargetException, verifica se o tipo field existe no map utilizado para fazer o parser. Caso este erro
     * apareça, basta implementar no parser o tipo que ele tentou processar e que não existe la
     * @throws IllegalAccessException, verificação requisitada pelo método set do field (field.set)
     * @throws NumberFormatException, verificação para o caso de se tentar transformar uma string em números e a string não possuir
     * o formato correto
     */
    private void processField(Field field, COL_TYPE column, Object entity) throws InvocationTargetException, IllegalAccessException, NumberFormatException {
        String strValue = getValueFromColumnAsString(column);
        Object value = new TypeParser().parse(field.getType(), strValue);
        field.setAccessible(Boolean.TRUE);
        field.set(entity, field.getType().cast(value));
    }

    /**
     * Método responsável por iniciar as informações de metaData. As metadas são as informações que serão usadas
     * para auxiliar no processamento do arquivo. O atributo foi criado visando evitar passar muitos parâmetro pelas
     * funções.
     *
     * @param file: arquivo a ser processado. De onde será recuperado o nome do arquivo
     * @param classe: classe base. De onde será recuperado o nome da classe
     */
    private void initMetaData(FileP file, Class classe) {
        this.metaData = new FileProcessorMetaData(file.getName(), classe.getSimpleName(), FileProcessorConstants.INITIAL_LINE, new ArrayList<>());
    }

    /**
     * Método responsável por encapsular o método getLinesFromFileAsList implementado pela classe que extender desta.
     * Como durante a execução dele pode ser que ocorra uma exceção pelo arquivo estar corrompido. é feito aqui o tratamento
     * desta exceção.
     *
     * @param file: parâmetro que contém as informações do arquivo que será processado
     *
     * @return List<LINE_TYPE>, contendo as informações de todas as linhas do arquivo a ser oricessadi
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo
     */
    private List<LINE_TYPE> getLines(FileP file) throws FileProcessException {
        try {
            return getLinesFromFileAsList(file);
        } catch (IOException e) {
            throw new FileProcessException(buildMessageExceptionInvalidFile(metaData.getFileName()), metaData.getLogErros(), FileProcessorExceptionType.INFALID_FILE);
        }
    }

    /**
     * Método resposável por retornar os fields válidos da classe base. Para isto foram considerados
     * fields válidos, todos aqueles que não forem estáticos e que não estiverem anotados com a
     * annotation @FileProcessorIgnore
     *
     * @param classe: classe que será tomada como base para o processamento do arquivo
     *
     * @return List<Field>, contendo todos os fields válidos encontrados na classe
     */
    private List<Field> getValidFields(Class classe) {
        return Arrays.stream(classe.getDeclaredFields()).filter(field -> !isStaticField(field) && !isToIgnoreField(field)).collect(Collectors.toList());
    }

    /**
     * Método que verifica se o field informado é um field estático
     *
     * @param field: field a ser verificado
     *
     * @return boolean, de modo que é retornado true no caso do field ser do tipo estático
     * e false caso contrário
     */
    private boolean isStaticField(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * Método que verifica se o field foi anotado com a annotation @FileProcessorIgnore
     *
     * @param field: field a ser verificado
     *
     * @return boolean, de modo que é retornado true no caso do field ter sido anotado com
     * a annotation e false caso contrário
     */
    private boolean isToIgnoreField(Field field) {
        return (field.getAnnotationsByType(FileProcessorIgnore.class).length > 0);
    }

    /**
     * Método responsável por remover as linhas desnecessárias para o processamento do arquivo. Para isto
     * foram consideradas linhas desnecessárias, todas aquelas que não possuem o numéro de colunas iguais
     * ao número de fields válidos da classe base informada
     *
     * @param lines: linhas recuperadas do arquivo
     * @param fields: fields válidos da classe base
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo.
     * Neste método é feito a validação do formato do arquivo, pois caso ao remover todas as linhas desnecessárias não
     * restarem linhas para serem processadas, isso quer dizer que o arquivo não está no formato correto para o processamento
     * da classe informada.
     * Sendo assim, duas coisas podem estar acontencendo, ou o arquivo realmente está errado, ou a classe informada possui mais
     * campos do que o arquivo está preenchendo, ou seja, deve-se anotar os campos que o arquivo não irá fornecer com @FileProcessorIgnore
     */
    private void removeUnnecessaryLines(List<LINE_TYPE> lines, List<Field> fields) throws FileProcessException {
        int index = 0;
        while (index < lines.size()) {
            updateLineMetaData();
            LINE_TYPE line = lines.get(index);
            List<COL_TYPE> columns = getColsFromLineAsList(line);
            if (columns.size() != fields.size()) {
                lines.remove(index);
            } else {
                index += 1;
            }
        }
        if (lines.isEmpty()) {
            metaData.getLogErros().add(buildMessageErrorIncorrectFileFormat(metaData));
            throw new FileProcessException(buildMessageExceptionNonstandardFile(metaData.getFileName()), metaData.getLogErros(), FileProcessorExceptionType.NONSTANDARD_FILE);
        }
    }

    /**
     * Método responsável por remover o header das. Assume-se que todos os arquivos possuem um header para identificar cada
     * coluna, e como o header não possui informações relevantes para o processamento ele deve ser removido
     *
     * @param lines: linhas recuperadas do arquivo
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo.
     * Neste método é feita a validação da completude do arquivo, pois caso após a remoção do header não sobrarem linhas
     * para serem processadas, significa que o arquivo está no formato certo, no entanto as linhas estão com informações
     * incompletas.
     */
    private void removeHeader(List<LINE_TYPE> lines) throws FileProcessException {
        updateLineMetaData();
        lines.remove(FileProcessorConstants.HEADER_INDEX);
        if (lines.isEmpty()) {
            metaData.getLogErros().add(buildMessageErrorIncorrectFileFormat(metaData));
            throw new FileProcessException(buildMessageExceptionIncompleteFile(metaData.getFileName()), metaData.getLogErros(), FileProcessorExceptionType.INCOMPLETE_FILE);
        }
    }

    /**
     * Método responsável por atualizar o contador de linha presente no metaData. Visando a melhor forma de identificar onde está o erro
     * no arquivo, foi colocado um contator de linha dentro do metaData, para quando ocorrer um Exception, ser printado no log onde o
     * erro foi encontrado de forma mais exata.
     */
    private void updateLineMetaData() {
        this.metaData.setLineCount(this.metaData.getLineCount()+1);
    }

    /**
     * Método responsável por criar uma instancia vazia da classe base do processamento
     *
     * @param classe: classe base do processamento
     *
     * @return Object, contendo uma instancia vazia da classe a ser processada
     *
     * @throws FileProcessException, validação para possiveis erros que podem ocorrer durante o processamento do arquivo.
     * Neste caso, é verificado se a classe base é possível de ser instanciada, caso não seja é iniciada uma Exception
     */
    private Object getEmptyInstanceOfEntity(Class classe) throws FileProcessException {
        try {
            return classe.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FileProcessException(buildMessageExceptionInvalidEntityClass(metaData.getEntityName()), metaData.getLogErros(), FileProcessorExceptionType.INVALID_ENTITY_CLASS);
        }
    }
}
