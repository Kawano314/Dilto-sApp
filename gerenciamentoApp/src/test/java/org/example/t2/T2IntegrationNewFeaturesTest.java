package org.example.t2;

import org.example.exceptions.ReservaSinucaException;
import org.example.models.HorarioFuncionamento;
import org.example.models.Produto;
import org.example.models.ReservaSinuca;
import org.example.service.HorarioService;
import org.example.service.ProdutoService;
import org.example.service.ReservaSinucaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integração T2 + Novas Funcionalidades")
class T2IntegrationNewFeaturesTest {
    private final HorarioService horarioService = new HorarioService();
    private final ReservaSinucaService reservaService = new ReservaSinucaService();
    private final ProdutoService produtoService = new ProdutoService();

    @Test
    @DisplayName("Feriado fechado bloqueia reserva; próxima data permite; relatório de estoque baixo funciona")
    void testFluxoIntegrado() throws Exception {
        LocalDate feriado = LocalDate.now().plusDays(20);
        try { horarioService.removerHorarioEspecial(feriado); } catch (Exception ignored) {}
        HorarioFuncionamento h = new HorarioFuncionamento();
        h.setDataEspecial(feriado);
        h.setFechado(true);
        h.setObservacao("Feriado de Integração");
        horarioService.cadastrarHorarioEspecial(h);

        ReservaSinuca rFeriado = new ReservaSinuca();
        rFeriado.setNumeroMesa(2);
        rFeriado.setDataReserva(feriado);
        rFeriado.setHoraInicio(LocalTime.of(10,0));
        rFeriado.setHoraFim(LocalTime.of(11,0));
        rFeriado.setNomeCliente("Cliente Feriado");
        rFeriado.setTelefoneCliente("99999999");
        assertThrows(ReservaSinucaException.class, () -> reservaService.criar(rFeriado));

        LocalDate proxima = feriado.plusDays(1);
        for (int i = 0; i < 7; i++) {
            var horario = reservaService.obterHorarioFuncionamento(proxima);
            if (horario != null && !horario.isFechado()) break;
            proxima = proxima.plusDays(1);
        }
        try { reservaService.cancelarTodasReservasDaMesa(2, proxima); } catch (Exception ignored) {}
        var horarioValido = reservaService.obterHorarioFuncionamento(proxima);
        assertNotNull(horarioValido);
        LocalTime inicio = horarioValido.getHorarioAbertura().plusHours(1);
        LocalTime fim = inicio.plusHours(1);
        ReservaSinuca rOK = new ReservaSinuca();
        rOK.setNumeroMesa(2);
        rOK.setDataReserva(proxima);
        rOK.setHoraInicio(inicio);
        rOK.setHoraFim(fim);
        rOK.setNomeCliente("Cliente OK");
        rOK.setTelefoneCliente("88888888");
        assertDoesNotThrow(() -> reservaService.criar(rOK));

        // Remove any existing products with the same names (by name)
        try {
            var existente1 = produtoService.buscarProdutosPorCategoria("Acessorios")
                    .stream().filter(p -> "Giz Profissional".equals(p.getNome())).findFirst();
            existente1.ifPresent(p -> { try { produtoService.deletarProduto(p.getId()); } catch (Exception ignored) {} });
        } catch (Exception ignored) {}
        try {
            var existente2 = produtoService.buscarProdutosPorCategoria("Acessorios")
                    .stream().filter(p -> "Pano Mesa Premium".equals(p.getNome())).findFirst();
            existente2.ifPresent(p -> { try { produtoService.deletarProduto(p.getId()); } catch (Exception ignored) {} });
        } catch (Exception ignored) {}

        Produto p1 = new Produto();
        p1.setNome("Giz Profissional");
        // codigo removed
        p1.setDescricao("Giz para tacos de sinuca");
        p1.setPreco(new BigDecimal("9.90"));
        p1.setQuantidadeEstoque(3);
        p1.setCategoria("Acessorios");
        assertDoesNotThrow(() -> produtoService.cadastrarProduto(p1));

        Produto p2 = new Produto();
        p2.setNome("Pano Mesa Premium");
        // codigo removed
        p2.setDescricao("Pano verde premium");
        p2.setPreco(new BigDecimal("199.90"));
        p2.setQuantidadeEstoque(10);
        p2.setCategoria("Acessorios");
        assertDoesNotThrow(() -> produtoService.cadastrarProduto(p2));

        List<Produto> baixos = assertDoesNotThrow(() -> produtoService.gerarRelatorioEstoqueBaixo(5));
        assertTrue(baixos.stream().anyMatch(prod -> "Giz Profissional".equals(prod.getNome())));
    }
}
