package processors;

import models.Carro;
import org.example.util.FileP;
import org.example.util.FileType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileProcessorCsvTest {

    private FileP getFileP() throws IOException {
        FileP fileP = new FileP();
        fileP.setName("base_dados_carros.csv");
        fileP.setContent(Files.readAllBytes(Path.of(System.getProperty("user.dir") + "\\src\\test\\resources\\base_dados_carros.csv")));
        return fileP;
    }

    @Test
    public void teste() throws Exception {
        List<Carro> result = (List<Carro>) FileType.CSV.process(getFileP(), Carro.class);
        Assert.assertEquals(result.size(), 3);
    }

}
