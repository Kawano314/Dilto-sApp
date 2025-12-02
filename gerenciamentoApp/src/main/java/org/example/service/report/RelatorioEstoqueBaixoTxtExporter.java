package org.example.service.report;

import org.example.models.Produto;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Exportador TXT para relatório de produtos com estoque baixo.
 */
public class RelatorioEstoqueBaixoTxtExporter implements RelatorioExporter<List<Produto>> {
    private final int limiteMinimo;

    public RelatorioEstoqueBaixoTxtExporter(int limiteMinimo) {
        if (limiteMinimo < 0) {
            throw new IllegalArgumentException("Limite mínimo deve ser >= 0");
        }
        this.limiteMinimo = limiteMinimo;
    }

    @Override
    public void exportar(List<Produto> produtos, Path destino) throws IOException {
        if (produtos == null) {
            throw new IllegalArgumentException("Lista de produtos não pode ser nula");
        }
        if (destino == null) {
            throw new IllegalArgumentException("Destino do relatório não pode ser nulo");
        }

        // Garante diretório
        Path parent = destino.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // Ordena do menor para o maior estoque
        List<Produto> ordenados = produtos.stream()
                .sorted(Comparator.comparingInt(p -> p.getQuantidadeEstoque() == null ? Integer.MAX_VALUE : p.getQuantidadeEstoque()))
                .toList();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try (BufferedWriter bw = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            bw.write("Relatório de Produtos com Estoque Baixo (<= " + limiteMinimo + ") - " + LocalDateTime.now().format(dtf));
            bw.newLine();
            bw.write("ID | Nome | Categoria | Preço | Estoque");
            bw.newLine();
            bw.write("------------------------------------------------------------");
            bw.newLine();

            for (Produto p : ordenados) {
                if (p.getQuantidadeEstoque() != null && p.getQuantidadeEstoque() <= limiteMinimo) {
                    String linha = String.format("%s | %s | %s | R$ %s | %s",
                            p.getId() == null ? "-" : p.getId(),
                            safe(p.getNome()),
                            safe(p.getCategoria()),
                            p.getPreco() == null ? "-" : p.getPreco().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                            p.getQuantidadeEstoque());
                    bw.write(linha);
                    bw.newLine();
                }
            }
        }
    }

    private String safe(String s) {
        return s == null ? "-" : s;
    }
}
