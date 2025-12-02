package org.example.t2;

import org.example.models.Produto;
import org.example.models.ReservaSinuca;
import org.example.service.ProdutoService;
import org.example.service.ReservaSinucaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validação de funcionalidades do T2 após integração com T3.
 * Confirma que as funcionalidades originais continuam funcionando.
 */
@DisplayName("Funcionalidades T2")
public class T2FunctionalitiesTest {

    private final ProdutoService produtoService = new ProdutoService();
    private final ReservaSinucaService reservaService = new ReservaSinucaService();

    @Test
    @DisplayName("CRUD de Produtos continua funcional após T3")
    void testCrudProdutos() throws Exception {
        // Limpar
        List<Produto> existentes = produtoService.listarTodosProdutos();
        for (Produto p : existentes) {
            try { produtoService.deletarProduto(p.getId()); } catch (Exception ignored) {}
        }

        // Cadastrar produto
        Produto p = new Produto();
        p.setNome("Teste T2");
        p.setPreco(new BigDecimal("15.00"));
        p.setQuantidadeEstoque(20);
        Produto criado = produtoService.cadastrarProduto(p);

        assertNotNull(criado.getId());
        assertEquals("Teste T2", criado.getNome());

        // Atualizar
        criado.setPreco(new BigDecimal("20.00"));
        produtoService.atualizarProduto(criado);

        // Buscar
        Produto encontrado = produtoService.buscarProdutoPorId(criado.getId());
        assertEquals(0, new BigDecimal("20.00").compareTo(encontrado.getPreco()));

        // Deletar
        produtoService.deletarProduto(criado.getId());
        assertThrows(Exception.class, () -> produtoService.buscarProdutoPorId(criado.getId()));
    }

    @Test
    @DisplayName("Validação de Reservas continua funcional após T3")
    void testValidacoesReserva() {
        // Rejeitar reserva passada
        assertThrows(Exception.class, () -> {
            ReservaSinuca r = new ReservaSinuca();
            r.setNumeroMesa(1);
            r.setDataReserva(LocalDate.now().minusDays(1));
            r.setHoraInicio(LocalTime.of(14, 0));
            r.setHoraFim(LocalTime.of(15, 0));
            r.setNomeCliente("Teste");
            r.setTelefoneCliente("99999999");
            reservaService.criar(r);
        });

        // Rejeitar horário inválido (fim antes do início)
        assertThrows(Exception.class, () -> {
            ReservaSinuca r = new ReservaSinuca();
            r.setNumeroMesa(1);
            r.setDataReserva(LocalDate.now().plusDays(1));
            r.setHoraInicio(LocalTime.of(15, 0));
            r.setHoraFim(LocalTime.of(14, 0));
            r.setNomeCliente("Teste");
            r.setTelefoneCliente("99999999");
            reservaService.criar(r);
        });

        // Aceitar reserva válida
        assertDoesNotThrow(() -> {
            ReservaSinuca r = new ReservaSinuca();
            r.setNumeroMesa(2);
            r.setDataReserva(LocalDate.now().plusDays(1));
            r.setHoraInicio(LocalTime.of(14, 0));
            r.setHoraFim(LocalTime.of(15, 0));
            r.setNomeCliente("Teste Valido");
            r.setTelefoneCliente("99999999");
            reservaService.criar(r);
        });
    }
}

