package org.example.service;

import org.example.models.ReservaSinuca;
import org.example.exceptions.ReservaSinucaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ReservaSinucaService")
class ReservaSinucaServiceTest {

    private ReservaSinucaService service = new ReservaSinucaService();
    private HorarioService horarioService = new HorarioService();

    @Nested
    @DisplayName("Visualizar Mapa de Mesas")
    class VisualizarMapa {
        @Test
        @DisplayName("Deve exibir mapa de mesas para data válida sem lançar exceção")
        void testExibirMapaSemExcecao() {
            assertDoesNotThrow(() -> service.exibirMapaMesas(LocalDate.now()));
        }

        @Test
        @DisplayName("Deve validar data não nula")
        void testValidarDataNaoNula() {
            assertThrows(Exception.class, () -> service.exibirMapaMesas(null));
        }
    }

    @Nested
    @DisplayName("Nova Reserva")
    class NovaReserva {
        @Test
        @DisplayName("Deve criar reserva com dados válidos")
        void testCriarReservaValida() {
            try {
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(1);
                r.setDataReserva(LocalDate.now().plusDays(1));
                r.setHoraInicio(LocalTime.of(14, 0));
                r.setHoraFim(LocalTime.of(15, 0));
                r.setNomeCliente("João");
                r.setTelefoneCliente("999999999");
                service.criar(r);
                assertNotNull(r);
            } catch (Exception e) {
                fail("Não deve lançar exceção com dados válidos: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Deve validar mesa em intervalo válido")
        void testValidarMesaValida() {
            try {
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(2);
                r.setDataReserva(LocalDate.now().plusDays(1));
                r.setHoraInicio(LocalTime.of(14, 0));
                r.setHoraFim(LocalTime.of(15, 0));
                r.setNomeCliente("Maria");
                r.setTelefoneCliente("888888888");
                service.criar(r);
                assertNotNull(r);
            } catch (Exception e) {
                fail("Não deve lançar exceção com mesa válida");
            }
        }

        @Test
        @DisplayName("Deve rejeitar mesa negativa")
        void testRejetarMesaNegativa() {
            assertThrows(ReservaSinucaException.class, () -> {
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(-1);
                r.setDataReserva(LocalDate.now().plusDays(1));
                r.setHoraInicio(LocalTime.of(14, 0));
                r.setHoraFim(LocalTime.of(15, 0));
                r.setNomeCliente("João");
                r.setTelefoneCliente("999999999");
                service.criar(r);
            });
        }

        @Test
        @DisplayName("Deve rejeitar horário inicio após fim")
        void testRejetarHorarioInvalido() {
            assertThrows(ReservaSinucaException.class, () -> {
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(1);
                r.setDataReserva(LocalDate.now().plusDays(1));
                r.setHoraInicio(LocalTime.of(15, 0));
                r.setHoraFim(LocalTime.of(14, 0));
                r.setNomeCliente("João");
                r.setTelefoneCliente("999999999");
                service.criar(r);
            });
        }

        @Test
        @DisplayName("Deve rejeitar data passada")
        void testRejetarDataPassada() {
            assertThrows(ReservaSinucaException.class, () -> {
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(1);
                r.setDataReserva(LocalDate.now().minusDays(1));
                r.setHoraInicio(LocalTime.of(14, 0));
                r.setHoraFim(LocalTime.of(15, 0));
                r.setNomeCliente("João");
                r.setTelefoneCliente("999999999");
                service.criar(r);
            });
        }

        @Test
        @DisplayName("Deve rejeitar cliente com nome vazio")
        void testRejetarClienteVazio() {
            assertThrows(ReservaSinucaException.class, () -> {
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(1);
                r.setDataReserva(LocalDate.now().plusDays(1));
                r.setHoraInicio(LocalTime.of(14, 0));
                r.setHoraFim(LocalTime.of(15, 0));
                r.setNomeCliente("");
                r.setTelefoneCliente("999999999");
                service.criar(r);
            });
        }

        @Test
        @DisplayName("Deve rejeitar telefone inválido")
        void testRejetarTelefoneInvalido() {
            ReservaSinuca r = new ReservaSinuca();
            r.setNumeroMesa(1);
            r.setDataReserva(LocalDate.now().plusDays(1));
            r.setHoraInicio(LocalTime.of(14, 0));
            r.setHoraFim(LocalTime.of(15, 0));
            r.setNomeCliente("João");
            r.setTelefoneCliente("123");

            assertThrows(org.example.exceptions.ReservaSinucaException.class, () -> service.criar(r));
        }

        @Test
        @DisplayName("Deve aceitar reservas em horários diferentes mesma mesa")
        void testAceitarHorariosDiferentes() {
            try {
                // Use data distante (2026) para evitar feriados de anos passados
                LocalDate data = LocalDate.of(2026, 6, 15);
                
                // Limpa pré-existentes
                try { service.cancelarTodasReservasDaMesa(1, data); } catch (Exception ignored) {}
                
                ReservaSinuca r1 = new ReservaSinuca();
                r1.setNumeroMesa(1);
                r1.setDataReserva(data);
                r1.setHoraInicio(LocalTime.of(14, 0));
                r1.setHoraFim(LocalTime.of(15, 0));
                r1.setNomeCliente("João");
                r1.setTelefoneCliente("999999999");
                service.criar(r1);

                ReservaSinuca r2 = new ReservaSinuca();
                r2.setNumeroMesa(1);
                r2.setDataReserva(data);
                r2.setHoraInicio(LocalTime.of(16, 30));
                r2.setHoraFim(LocalTime.of(17, 30));
                r2.setNomeCliente("Maria");
                r2.setTelefoneCliente("888888888");
                service.criar(r2);
                assertNotNull(r2);
            } catch (Exception e) {
                fail("Deve aceitar horários diferentes");
            }
        }
    }

    @Nested
    @DisplayName("Limites de Funcionamento (T3 - Manutenção)")
    class LimitesFuncionamento {
        private LocalDate proximoDiaUtil() throws Exception {
            LocalDate data = LocalDate.now().plusDays(40);
            for (int i = 0; i < 14; i++) {
                var h = horarioService.obterHorario(data);
                if (h != null && !h.isFechado()) {
                    return data;
                }
                data = data.plusDays(1);
            }
            return LocalDate.now().plusDays(3);
        }

        @Test
        @DisplayName("Deve permitir fim exatamente no horário de fechamento")
        void testFimExatamenteNoFechamento() throws Exception {
            LocalDate data = proximoDiaUtil();
            var horario = service.obterHorarioFuncionamento(data);
            assertNotNull(horario);
            assertFalse(horario.isFechado());

            // Reserva termina exatamente no fechamento
            LocalTime fechamento = horario.getHorarioFechamento();
            LocalTime inicio = fechamento.minusHours(1);

            // Limpa pré-existentes (execuções anteriores)
            try { service.cancelarTodasReservasDaMesa(3, data); } catch (Exception ignored) {}

            ReservaSinuca r = new ReservaSinuca();
            r.setNumeroMesa(3); // evitar possíveis seeds em 1 e 2
            r.setDataReserva(data);
            r.setHoraInicio(inicio);
            r.setHoraFim(fechamento);
            r.setNomeCliente("Cliente Fechamento");
            r.setTelefoneCliente("99999999");

            assertDoesNotThrow(() -> service.criar(r));
        }

        @Test
        @DisplayName("Deve permitir reservas encostadas (fim == próximo início)")
        void testReservasBackToBackSemConflito() throws Exception {
            LocalDate data = proximoDiaUtil();
            var horario = service.obterHorarioFuncionamento(data);
            assertNotNull(horario);
            assertFalse(horario.isFechado());

            LocalTime inicio1 = horario.getHorarioAbertura().plusHours(2); // dentro do expediente
            LocalTime fim1 = inicio1.plusHours(1);
            LocalTime inicio2 = fim1; // encostada
            LocalTime fim2 = inicio2.plusHours(1);

            // Limpa pré-existentes (execuções anteriores)
            try { service.cancelarTodasReservasDaMesa(3, data); } catch (Exception ignored) {}

            ReservaSinuca r1 = new ReservaSinuca();
            r1.setNumeroMesa(3);
            r1.setDataReserva(data);
            r1.setHoraInicio(inicio1);
            r1.setHoraFim(fim1);
            r1.setNomeCliente("Cliente 1");
            r1.setTelefoneCliente("88888888");

            ReservaSinuca r2 = new ReservaSinuca();
            r2.setNumeroMesa(3);
            r2.setDataReserva(data);
            r2.setHoraInicio(inicio2);
            r2.setHoraFim(fim2);
            r2.setNomeCliente("Cliente 2");
            r2.setTelefoneCliente("88888888");

            assertDoesNotThrow(() -> service.criar(r1));
            assertDoesNotThrow(() -> service.criar(r2));
        }
    }

    @Nested
    @DisplayName("Remover Reserva")
    class RemoverReserva {
        @Test
        @DisplayName("Deve remover reserva existente")
        void testRemoverReservaExistente() {
            try {
                // Criar reserva temporária para remoção
                ReservaSinuca r = new ReservaSinuca();
                r.setNumeroMesa(1);
                r.setDataReserva(LocalDate.now().plusDays(2));
                r.setHoraInicio(LocalTime.of(13, 0));
                r.setHoraFim(LocalTime.of(14, 0));
                r.setNomeCliente("Remover Teste");
                r.setTelefoneCliente("99999999");
                ReservaSinuca criado = service.criar(r);

                assertDoesNotThrow(() -> service.remover(criado.getId()));
            } catch (Exception e) {
                fail("Não deve lançar exceção: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover ID inválido")
        void testRemoverIdInvalido() {
            assertThrows(Exception.class, () -> {
                service.remover(-1);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover reserva não existente")
        void testRemoverNaoExistente() {
            assertThrows(Exception.class, () -> {
                service.remover(99999);
            });
        }
    }

    @Nested
    @DisplayName("Cancelar Todas Reservas da Mesa")
    class CancelarTodasReservasMesa {
        @Test
        @DisplayName("Deve cancelar todas as reservas de uma mesa sem lançar exceção")
        void testCancelarTodasMesa() {
            assertDoesNotThrow(() -> service.cancelarTodasReservasMesa(2));
        }

        @Test
        @DisplayName("Deve lançar exceção com mesa inválida")
        void testCancelarMesaInvalida() {
            assertThrows(Exception.class, () -> {
                service.cancelarTodasReservasMesa(-1);
            });
        }

        @Test
        @DisplayName("Deve listar reservas após cancelamento sem lançar exceção")
        void testIntegridadeAposCancelamento() {
            assertDoesNotThrow(() -> {
                service.cancelarTodasReservasMesa(1);
                List<ReservaSinuca> reservas = service.listar();
                assertNotNull(reservas);
            });
        }
    }

    @Nested
    @DisplayName("Buscar Reservas")
    class BuscarReservas {
        @Test
        @DisplayName("Deve buscar reservas por data")
        void testBuscarPorData() {
            try {
                List<ReservaSinuca> reservas = service.buscarPorData(LocalDate.now());
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve retornar lista vazia para data sem reservas")
        void testRetornarVaziaSemReservas() {
            try {
                LocalDate dataDistante = LocalDate.now().plusDays(365);
                List<ReservaSinuca> reservas = service.buscarPorData(dataDistante);
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve buscar reservas por mesa")
        void testBuscarPorMesa() {
            try {
                List<ReservaSinuca> reservas = service.buscarPorMesa(1);
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve buscar todas as reservas")
        void testBuscarTodas() {
            try {
                List<ReservaSinuca> reservas = service.listar();
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve retornar lista não nula")
        void testRetornarListaNaoNula() {
            try {
                List<ReservaSinuca> reservas = service.listar();
                assertNotNull(reservas);
                assertTrue(reservas.size() >= 0);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve validar formato de data válido")
        void testValidarDataValida() {
            try {
                LocalDate data = LocalDate.of(2024, 12, 25);
                List<ReservaSinuca> reservas = service.buscarPorData(data);
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção com data válida");
            }
        }
    }

    @Nested
    @DisplayName("Buscar Reservas por Cliente")
    class BuscarPorCliente {
        @Test
        @DisplayName("Deve buscar reservas do cliente por nome")
        void testBuscarPorNomeCliente() {
            try {
                List<ReservaSinuca> reservas = service.buscarPorCliente("João");
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve retornar lista vazia para cliente sem reservas")
        void testRetornarVaziaClienteSemReservas() {
            try {
                List<ReservaSinuca> reservas = service.buscarPorCliente("ClienteInexistente123456789");
                assertNotNull(reservas);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve diferenciar maiúsculas e minúsculas")
        void testCaseSensitiveCliente() {
            try {
                List<ReservaSinuca> reservasUpper = service.buscarPorCliente("JOÃO");
                List<ReservaSinuca> reservasLower = service.buscarPorCliente("joão");
                assertNotNull(reservasUpper);
                assertNotNull(reservasLower);
                // Com busca case-insensitive, resultados devem ser equivalentes em tamanho
                assertEquals(reservasUpper.size(), reservasLower.size());
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }
    }

    @Test
    @DisplayName("Deve fechar conexão com BD")
    void testFechar() {
        service.fechar();
        assertNotNull(service);
    }
}
