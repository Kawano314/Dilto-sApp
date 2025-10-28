package org.example.jsoncreator;

import org.example.service.ProdutoService;
import org.example.models.Produto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.util.List;

public class ProdutoJSONExportar {
    public static void exportarParaJSON(String caminhoArquivo) {
        try {
            ProdutoService produtoService = new ProdutoService();
            List<Produto> produtos = produtoService.listarTodosProdutos();

            ObjectMapper mapper = new ObjectMapper();

            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(caminhoArquivo), produtos);
        } catch (Exception e) {
            System.err.println("✗ Erro ao exportar JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
