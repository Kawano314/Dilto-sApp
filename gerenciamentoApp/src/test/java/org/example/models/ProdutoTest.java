package org.example.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Produto")
class ProdutoTest {

    @Test
    @DisplayName("Deve criar produto com todos os campos")
    void testCriarProdutoCompleto() {
        Produto p = new Produto("Refrigerante", new BigDecimal("8.50"), 50);
        assertEquals("Refrigerante", p.getNome());
        assertEquals(new BigDecimal("8.50"), p.getPreco());
        assertEquals(50, p.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar produto vazio")
    void testCriarProdutoVazio() {
        Produto p = new Produto();
        assertNotNull(p);
        assertNull(p.getNome());
        assertNotNull(p.getDataCadastro());
    }

    @Test
    @DisplayName("Deve atualizar nome do produto")
    void testAtualizarNome() {
        Produto p = new Produto("Refrigerante", new BigDecimal("8.50"), 50);
        p.setNome("Suco Natural");
        assertEquals("Suco Natural", p.getNome());
        assertNotNull(p.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve atualizar preço do produto")
    void testAtualizarPreco() {
        Produto p = new Produto("Refrigerante", new BigDecimal("8.50"), 50);
        p.setPreco(new BigDecimal("9.99"));
        assertEquals(new BigDecimal("9.99"), p.getPreco());
    }

    @Test
    @DisplayName("Deve atualizar quantidade em estoque")
    void testAtualizarQuantidade() {
        Produto p = new Produto("Refrigerante", new BigDecimal("8.50"), 50);
        p.setQuantidadeEstoque(100);
        assertEquals(100, p.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve atualizar categoria")
    void testAtualizarCategoria() {
        Produto p = new Produto("Refrigerante", new BigDecimal("8.50"), 50);
        p.setCategoria("Bebidas");
        assertEquals("Bebidas", p.getCategoria());
    }

    @Test
    @DisplayName("Deve atualizar descrição")
    void testAtualizarDescricao() {
        Produto p = new Produto("Refrigerante", new BigDecimal("8.50"), 50);
        p.setDescricao("Refrigerante gelado sabor cola");
        assertEquals("Refrigerante gelado sabor cola", p.getDescricao());
    }

    @Test
    @DisplayName("Deve validar ID do produto")
    void testValidarId() {
        Produto p = new Produto();
        assertNull(p.getId());
        p.setId(1L);
        assertEquals(1L, p.getId());
    }

    @Test
    @DisplayName("Deve validar datas de cadastro e atualização")
    void testValidarDatas() {
        Produto p = new Produto();
        assertNotNull(p.getDataCadastro());
        assertNotNull(p.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve aceitar preço nulo")
    void testPrecoNulo() {
        Produto p = new Produto();
        p.setPreco(null);
        assertNull(p.getPreco());
    }

    @Test
    @DisplayName("Deve aceitar quantidade estoque negativa")
    void testQuantidadeNegativa() {
        Produto p = new Produto();
        p.setQuantidadeEstoque(-5);
        assertEquals(-5, p.getQuantidadeEstoque());
    }
}
