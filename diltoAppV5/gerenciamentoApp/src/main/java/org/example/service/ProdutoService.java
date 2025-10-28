package org.example.service;

import org.example.dao.ProdutoDAO;
import org.example.exceptions.ProdutoException;
import org.example.models.Produto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProdutoService {
    private ProdutoDAO produtoDAO;

    public ProdutoService() {
        this.produtoDAO = new ProdutoDAO();
    }

    public Produto cadastrarProduto(Produto produto) throws ProdutoException {
        try {
            validarProduto(produto);
            try {
                Produto existente = produtoDAO.buscarPorCodigo(produto.getCodigo());
                if (existente != null) {
                    throw new ProdutoException("Já existe um produto com o código: " + produto.getCodigo());
                }
            } catch (SQLException e) {
            }

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

    public Produto buscarProdutoPorCodigo(String codigo) throws ProdutoException {
        try {
            Produto produto = produtoDAO.buscarPorCodigo(codigo);
            if (produto == null) {
                throw new ProdutoException("Produto não encontrado com código: " + codigo);
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

    public List<Produto> buscarProdutosPorNome(String nome) throws ProdutoException {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                throw new ProdutoException("Nome não pode ser vazio");
            }
            return produtoDAO.buscarPorNome(nome);
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produtos por nome: " + e.getMessage(), e);
        }
    }

    public List<Produto> buscarProdutosPorCategoria(String categoria) throws ProdutoException {
        try {
            return produtoDAO.buscarPorCategoria(categoria);
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
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

    public void adicionarEstoque(Long id, int quantidade) throws ProdutoException {
        try {
            if (quantidade <= 0) {
                throw new ProdutoException("Quantidade deve ser maior que zero");
            }

            Produto produto = produtoDAO.buscarPorId(id);
            if (produto == null) {
                throw new ProdutoException("Produto não encontrado com ID: " + id);
            }

            produtoDAO.atualizarEstoque(id, quantidade);
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao adicionar estoque: " + e.getMessage(), e);
        }
    }

    public void removerEstoque(Long id, int quantidade) throws ProdutoException {
        try {
            if (quantidade <= 0) {
                throw new ProdutoException("Quantidade deve ser maior que zero");
            }

            Produto produto = produtoDAO.buscarPorId(id);
            if (produto == null) {
                throw new ProdutoException("Produto não encontrado com ID: " + id);
            }

            if (produto.getQuantidadeEstoque() < quantidade) {
                throw new ProdutoException("Estoque insuficiente. Disponível: " +
                        produto.getQuantidadeEstoque() + ", Solicitado: " + quantidade);
            }

            produtoDAO.atualizarEstoque(id, -quantidade);
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao remover estoque: " + e.getMessage(), e);
        }
    }

    private void validarProduto(Produto produto) throws ProdutoException {
        if (produto == null) {
            throw new ProdutoException("Produto não pode ser nulo");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ProdutoException("Nome do produto é obrigatório");
        }

        if (produto.getCodigo() == null || produto.getCodigo().trim().isEmpty()) {
            throw new ProdutoException("Código do produto é obrigatório");
        }

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
}