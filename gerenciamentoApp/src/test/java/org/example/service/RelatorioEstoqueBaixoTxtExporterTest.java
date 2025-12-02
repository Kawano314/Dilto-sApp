package org.example.service;

import org.example.models.Produto;
import org.example.service.report.RelatorioEstoqueBaixoTxtExporter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TDD - Exportação TXT de Estoque Baixo")
class RelatorioEstoqueBaixoTxtExporterTest {

    private ProdutoService service;
    private Path saida;

    @BeforeEach
    void setup() throws Exception {
        service = new ProdutoService();
        // Limpa base
        try {
            for (Produto p : service.listarTodosProdutos()) {
                try { service.deletarProduto(p.getId()); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // Cadastra produtos variados
        cadastrar("Giz Azul", "9.90", 3, "Acessorios");
        cadastrar("Taco Standard", "120.00", 15, "Acessorios");
        cadastrar("Bola nº 8", "29.90", 2, "Acessorios");

        // Caminho de saída de teste
        saida = Path.of("target", "test-output", "relatorio-estoque-baixo.txt");
        try { Files.deleteIfExists(saida); } catch (IOException ignored) {}
    }

    private void cadastrar(String nome, String preco, int estoque, String categoria) throws Exception {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPreco(new BigDecimal(preco));
        p.setQuantidadeEstoque(estoque);
        p.setCategoria(categoria);
        service.cadastrarProduto(p);
    }

    @AfterEach
    void teardown() {
        service.fechar();
        try { if (saida != null) Files.deleteIfExists(saida); } catch (IOException ignored) {}
    }

    @Test
    @DisplayName("Deve exportar TXT com itens <= limite, ordenados por estoque")
    void deveExportarTxtOrdenado() throws Exception {
        int limite = 5;
        var exporter = new RelatorioEstoqueBaixoTxtExporter(limite);
        var dados = service.gerarRelatorioEstoqueBaixo(limite);

        assertDoesNotThrow(() -> exporter.exportar(dados, saida));

        assertTrue(Files.exists(saida));
        String conteudo = Files.readString(saida);
        assertTrue(conteudo.contains("Relatório de Produtos com Estoque Baixo"));
        assertTrue(conteudo.contains("Giz Azul"));
        assertTrue(conteudo.contains("Bola nº 8"));
        assertFalse(conteudo.contains("Taco Standard")); // acima do limite

        // Verifica ordenação: estoque 2 vem antes do 3
        int idxBola = conteudo.indexOf("Bola nº 8");
        int idxGiz = conteudo.indexOf("Giz Azul");
        assertTrue(idxBola >= 0 && idxGiz >= 0);
        assertTrue(idxBola < idxGiz);
    }

    @Test
    @DisplayName("Service deve exportar usando interface de exportação")
    void serviceDeveExportarTxt() throws Exception {
        int limite = 5;
        assertDoesNotThrow(() -> service.exportarRelatorioEstoqueBaixoTxt(limite, saida));
        assertTrue(Files.exists(saida));
        String conteudo = Files.readString(saida);
        assertTrue(conteudo.startsWith("Relatório de Produtos com Estoque Baixo"));
    }
}
