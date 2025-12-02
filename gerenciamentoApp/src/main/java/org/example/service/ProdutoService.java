package org.example.service;

import org.example.dao.ProdutoDAO;
import org.example.exceptions.ProdutoException;
import org.example.models.Produto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.nio.file.Path;
import java.io.IOException;
import org.example.service.report.RelatorioExporter;
import org.example.service.report.RelatorioEstoqueBaixoTxtExporter;

public class ProdutoService {
    private ProdutoDAO produtoDAO;

    public ProdutoService() {
        this.produtoDAO = new ProdutoDAO();
    }

    public Produto cadastrarProduto(Produto produto) throws ProdutoException {
        try {
            validarProduto(produto);
            // No longer uses 'codigo' field; uniqueness checks removed

            return produtoDAO.inserir(produto);
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }

    public Produto buscarProdutoPorId(Long id) throws ProdutoException {
        try {
            Produto produto = produtoDAO.buscarPorId(id);
            if (produto == null) {
                throw new ProdutoException("Produto não encontrado com ID: " + id);
            }
            return produto;
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarTodosProdutos() throws ProdutoException {
        try {
            return produtoDAO.listarTodos();
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao listar produtos: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarProdutosEstoqueBaixo(int limiteMinimo) throws ProdutoException {
        try {
            return produtoDAO.buscarEstoqueBaixo(limiteMinimo);
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produtos com estoque baixo: " + e.getMessage(), e);
        }
    }

    public void atualizarProduto(Produto produto) throws ProdutoException {
        try {
            validarProduto(produto);

            if (produto.getId() == null) {
                throw new ProdutoException("ID do produto não pode ser nulo");
            }

            Produto existente = produtoDAO.buscarPorId(produto.getId());
            if (existente == null) {
                throw new ProdutoException("Produto não encontrado com ID: " + produto.getId());
            }

            boolean atualizado = produtoDAO.atualizar(produto);
            if (!atualizado) {
                throw new ProdutoException("Falha ao atualizar produto");
            }
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void deletarProduto(Long id) throws ProdutoException {
        try {
            Produto existente = produtoDAO.buscarPorId(id);
            if (existente == null) {
                throw new ProdutoException("Produto não encontrado com ID: " + id);
            }

            boolean deletado = produtoDAO.deletar(id);
            if (!deletado) {
                throw new ProdutoException("Falha ao deletar produto");
            }
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao deletar produto: " + e.getMessage(), e);
        }
    }

    private void validarProduto(Produto produto) throws ProdutoException {
        if (produto == null) {
            throw new ProdutoException("Produto não pode ser nulo");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ProdutoException("Nome do produto é obrigatório");
        }

            // 'codigo' removed from domain model; no validation required

        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProdutoException("Preço deve ser maior que zero");
        }

        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < 0) {
            throw new ProdutoException("Quantidade em estoque não pode ser negativa");
        }
    }

    public void fechar() {
        produtoDAO.fechar();
    }

    /**
     * Busca produtos dentro de uma faixa de preço
     * @param precoMinimo Preço mínimo (inclusivo)
     * @param precoMaximo Preço máximo (inclusivo)
     * @return Lista de produtos na faixa especificada
     * @throws ProdutoException Se os parâmetros forem inválidos
     */
    public List<Produto> buscarPorFaixaPreco(BigDecimal precoMinimo, BigDecimal precoMaximo) throws ProdutoException {
        if (precoMinimo == null || precoMaximo == null) {
            throw new ProdutoException("Preços mínimo e máximo são obrigatórios");
        }
        
        if (precoMinimo.compareTo(precoMaximo) > 0) {
            throw new ProdutoException("Preço mínimo deve ser menor ou igual ao preço máximo");
        }
        
        try {
            List<Produto> todosProdutos = produtoDAO.listarTodos();
            return todosProdutos.stream()
                .filter(p -> p.getPreco().compareTo(precoMinimo) >= 0 && 
                           p.getPreco().compareTo(precoMaximo) <= 0)
                .toList();
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produtos por faixa de preço: " + e.getMessage(), e);
        }
    }

    /**
     * Gera relatório de produtos com estoque baixo
     * Alias otimizado para listarProdutosEstoqueBaixo (delega diretamente ao DAO para eficiência)
     * @param limiteMinimo Quantidade mínima de estoque considerada adequada
     * @return Lista de produtos com estoque abaixo do limite, ordenados do menor para o maior
     * @throws ProdutoException Se o limite for inválido
     */
    public List<Produto> gerarRelatorioEstoqueBaixo(int limiteMinimo) throws ProdutoException {
        if (limiteMinimo < 0) {
            throw new ProdutoException("Limite mínimo deve ser maior ou igual a zero");
        }
        return listarProdutosEstoqueBaixo(limiteMinimo);
    }

    /**
     * Exporta relatório TXT de produtos com estoque baixo para o caminho informado.
     * Este método é um atalho que combina a listagem via DAO e a exportação via interface.
     * @param limiteMinimo Limite de estoque considerado baixo
     * @param destino Caminho do arquivo TXT de saída
     * @return O próprio caminho de destino, para conveniência de encadeamento
     * @throws ProdutoException Em caso de validação/IO/SQL
     */
    public Path exportarRelatorioEstoqueBaixoTxt(int limiteMinimo, Path destino) throws ProdutoException {
        if (limiteMinimo < 0) {
            throw new ProdutoException("Limite mínimo deve ser maior ou igual a zero");
        }
        if (destino == null) {
            throw new ProdutoException("Destino do relatório não pode ser nulo");
        }
        try {
            List<Produto> baixos = listarProdutosEstoqueBaixo(limiteMinimo);
            RelatorioExporter<List<Produto>> exporter = new RelatorioEstoqueBaixoTxtExporter(limiteMinimo);
            exporter.exportar(baixos, destino);
            return destino;
        } catch (IOException e) {
            throw new ProdutoException("Erro ao exportar relatório TXT: " + e.getMessage(), e);
        }
    }

    /**
     * Busca produtos por categoria
     * @param categoria Categoria dos produtos a buscar
     * @return Lista de produtos da categoria especificada
     * @throws ProdutoException Se houver erro na busca
     */
    public List<Produto> buscarProdutosPorCategoria(String categoria) throws ProdutoException {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new ProdutoException("Categoria não pode ser vazia");
        }
        
        try {
            List<Produto> todosProdutos = produtoDAO.listarTodos();
            return todosProdutos.stream()
                .filter(p -> p.getCategoria() != null && 
                           p.getCategoria().equalsIgnoreCase(categoria.trim()))
                .toList();
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        }
    }
}