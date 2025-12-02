package org.example.dao;

import org.example.models.ConnectDB;
import org.example.models.Produto;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    private ConnectDB connectDB;

    public ProdutoDAO() {
        this.connectDB = new ConnectDB();
        criarTabela();
    }

    private void criarTabela() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produtos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                descricao TEXT,
                preco REAL NOT NULL,
                quantidade_estoque INTEGER NOT NULL DEFAULT 0,
                categoria TEXT,
                data_cadastro TEXT NOT NULL,
                data_atualizacao TEXT NOT NULL
            )
        """;

        try (Statement stmt = connectDB.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("✗ Erro ao criar tabela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Produto inserir(Produto produto) throws SQLException {
        String sql = """
            INSERT INTO produtos (nome, descricao, preco, quantidade_estoque, 
                                 categoria, data_cadastro, data_atualizacao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco().doubleValue());
            pstmt.setInt(4, produto.getQuantidadeEstoque());
            pstmt.setString(5, produto.getCategoria());
            pstmt.setString(6, produto.getDataCadastro().toString());
            pstmt.setString(7, produto.getDataAtualizacao().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produto.setId(generatedKeys.getLong(1));
                    }
                }
            }

            return produto;
        }
    }

    public Produto buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairProdutoDoResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";

        try (Statement stmt = connectDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produtos.add(extrairProdutoDoResultSet(rs));
            }
        }
        return produtos;
    }

    public List<Produto> buscarEstoqueBaixo(int limiteMinimo) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE quantidade_estoque <= ? ORDER BY quantidade_estoque";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, limiteMinimo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                produtos.add(extrairProdutoDoResultSet(rs));
            }
        }
        return produtos;
    }

    public boolean atualizar(Produto produto) throws SQLException {
        String sql = """
            UPDATE produtos 
            SET nome = ?, descricao = ?, preco = ?, quantidade_estoque = ?, 
                categoria = ?, data_atualizacao = ?
            WHERE id = ?
        """;

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco().doubleValue());
            pstmt.setInt(4, produto.getQuantidadeEstoque());
            pstmt.setString(5, produto.getCategoria());
            pstmt.setString(6, LocalDateTime.now().toString());
            pstmt.setLong(7, produto.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(Long id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (PreparedStatement pstmt = connectDB.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Produto extrairProdutoDoResultSet(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(BigDecimal.valueOf(rs.getDouble("preco")));
        produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        produto.setCategoria(rs.getString("categoria"));
        produto.setDataCadastro(LocalDateTime.parse(rs.getString("data_cadastro")));
        produto.setDataAtualizacao(LocalDateTime.parse(rs.getString("data_atualizacao")));
        return produto;
    }

    public void fechar() {
        connectDB.closeConnection();
    }
}