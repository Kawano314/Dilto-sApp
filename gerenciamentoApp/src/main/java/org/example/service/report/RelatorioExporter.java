package org.example.service.report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface genérica para exportação de relatórios.
 * Implementações específicas podem exportar em diferentes formatos (TXT, CSV, etc.).
 */
public interface RelatorioExporter<T> {
    /**
     * Exporta os dados para o destino informado.
     * @param dados Dados a exportar
     * @param destino Caminho de saída do relatório (arquivo)
     * @throws IOException Em caso de erro de escrita
     */
    void exportar(T dados, Path destino) throws IOException;
}
