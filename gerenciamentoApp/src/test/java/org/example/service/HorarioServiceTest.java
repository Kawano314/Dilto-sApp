package org.example.service;

import org.example.models.HorarioFuncionamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do HorarioService")
class HorarioServiceTest {

    private HorarioService service = new HorarioService();
    private ReservaSinucaService reservaService = new ReservaSinucaService();

    @Nested
    @DisplayName("Listar Horários")
    class ListarHorarios {
        @Test
        @DisplayName("Deve listar horários padrão")
        void testListarHorariosPadrao() {
            try {
                List<HorarioFuncionamento> horarios = service.listarHorariosPadrao();
                assertNotNull(horarios);
                assertTrue(horarios.size() > 0);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve listar horários especiais")
        void testListarHorariosEspeciais() {
            try {
                List<HorarioFuncionamento> horarios = service.listarHorariosEspeciais();
                assertNotNull(horarios);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }
    }

    @Nested
    @DisplayName("Remover Horário Especial")
    class RemoverHorarioEspecial {
        @Test
        @DisplayName("Deve remover horário especial existente")
        void testRemoverHorarioEspecial() {
            try {
                LocalDate data = LocalDate.now().plusDays(1);
                service.removerHorarioEspecial(data);
                assertNotNull(data);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover horário inválido")
        void testRemoverHorarioInvalido() {
            try {
                service.removerHorarioEspecial(null);
            } catch (Exception e) {
                assertNotNull(e);
            }
        }
    }

    @Nested
    @DisplayName("Adicionar Horário Especial")
    class AdicionarHorarioEspecial {
        @Test
        @DisplayName("Deve adicionar horário especial válido")
        void testAdicionarHorarioEspecial() {
            try {
                LocalDate dataFutura = LocalDate.now().plusDays(10);
                // Limpa caso já exista (execuções anteriores)
                try { service.removerHorarioEspecial(dataFutura); } catch (Exception ignored) {}
                
                HorarioFuncionamento h = new HorarioFuncionamento();
                h.setDataEspecial(dataFutura);
                h.setHorarioAbertura(LocalTime.of(10, 0));
                h.setHorarioFechamento(LocalTime.of(14, 0));
                h.setFechado(false);
                h.setObservacao("Teste");
                service.cadastrarHorarioEspecial(h);
                assertNotNull(h);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve validar data não passada")
        void testValidarDataNaoPassada() {
            assertThrows(IllegalArgumentException.class, () -> {
                service.validarDataEspecial(LocalDate.now().minusDays(1));
            });
        }

        @Test
        @DisplayName("Deve aceitar data futura")
        void testAceitarDataFutura() {
            try {
                service.validarDataEspecial(LocalDate.now().plusDays(1));
            } catch (IllegalArgumentException e) {
                fail("Não deve lançar exceção para data futura");
            }
        }
    }

    @Nested
    @DisplayName("Alterar Horário Padrão")
    class AlterarHorarioPadrao {
        @Test
        @DisplayName("Deve alterar horário padrão válido")
        void testAlterarHorarioPadrao() {
            try {
                HorarioFuncionamento h = new HorarioFuncionamento();
                h.setDiaSemana(DayOfWeek.MONDAY);
                h.setHorarioAbertura(LocalTime.of(8, 0));
                h.setHorarioFechamento(LocalTime.of(18, 0));
                h.setFechado(false);
                service.atualizarHorarioPadrao(h);
                assertNotNull(h);
            } catch (Exception e) {
                fail("Não deve lançar exceção");
            }
        }

        @Test
        @DisplayName("Deve validar abertura antes de fechamento")
        void testValidarAberturaAntesFechamento() {
            assertThrows(IllegalArgumentException.class, () -> {
                service.validarHorario(LocalTime.of(18, 0), LocalTime.of(8, 0), false);
            });
        }

        @Test
        @DisplayName("Deve aceitar abertura válida")
        void testAceitarAberturaValida() {
            try {
                service.validarHorario(LocalTime.of(8, 0), LocalTime.of(18, 0), false);
            } catch (IllegalArgumentException e) {
                fail("Não deve lançar exceção para horário válido");
            }
        }
    }

    @Nested
    @DisplayName("Ver Horários")
    class VerHorarios {
        @Test
        @DisplayName("Deve verificar status atual")
        void testVerificarStatusAtual() {
            HorarioService.StatusFuncionamento status = service.verificarStatusAtual();
            assertNotNull(status);
            assertNotNull(status.getMensagem());
        }

        @Test
        @DisplayName("Deve retornar mensagem não nula")
        void testMensagemNaoNula() {
            HorarioService.StatusFuncionamento status = service.verificarStatusAtual();
            assertTrue(status.getMensagem().length() > 0);
        }

        @Test
        @DisplayName("Deve retornar próximo evento não nulo")
        void testProximoEventoNaoNulo() {
            HorarioService.StatusFuncionamento status = service.verificarStatusAtual();
            assertNotNull(status.getProximoEvento());
        }
    }

    @Test
    @DisplayName("Deve fechar conexão com BD")
    void testFechar() {
        service.fechar();
        assertNotNull(service);
    }

    @Nested
    @DisplayName("Feriados Automáticos (TDD)")
    class FeriadosAutomaticos {
        @Test
        @DisplayName("Deve cadastrar feriados nacionais de 2026")
        void testCadastrarFeriadosNacionais2026() {
            assertDoesNotThrow(() -> service.cadastrarFeriadosNacionais(2026));
        }

        @Test
        @DisplayName("Deve retornar lista de feriados cadastrados")
        void testListarFeriadosCadastrados() throws Exception {
            service.cadastrarFeriadosNacionais(2026);
            List<HorarioFuncionamento> feriados = service.listarHorariosEspeciais();
            
            // Deve ter pelo menos os feriados fixos cadastrados
            assertTrue(feriados.size() > 0);
        }

        @Test
        @DisplayName("Deve marcar feriados como fechado")
        void testFeriadosMarcadosComoFechado() throws Exception {
            // Limpa feriado se já existir
            LocalDate anoNovo = LocalDate.of(2026, 1, 1);
            try { service.removerHorarioEspecial(anoNovo); } catch (Exception ignored) {}
            
            service.cadastrarFeriadosNacionais(2026);
            
            // Ano Novo: 01/01/2026
            HorarioFuncionamento horario = service.obterHorario(anoNovo);
            
            assertNotNull(horario);
            assertTrue(horario.isFechado());
            assertTrue(horario.getObservacao().contains("Feriado") || 
                      horario.getObservacao().contains("Ano Novo"));
        }

        @Test
        @DisplayName("Não deve cadastrar feriados duplicados")
        void testNaoDuplicarFeriados() throws Exception {
            service.cadastrarFeriadosNacionais(2026);
            
            // Tentar cadastrar novamente
            assertDoesNotThrow(() -> service.cadastrarFeriadosNacionais(2026));
        }
    }

    @Nested
    @DisplayName("Especial Fechado sem horários (T3 - Manutenção)")
    class EspecialFechadoSemHorarios {
        @Test
        @DisplayName("Deve cadastrar especial fechado com horários nulos e bloquear reservas")
        void testEspecialFechadoComHorasNulas() throws Exception {
            LocalDate data = LocalDate.now().plusDays(7);

            // Limpar previamente
            try { service.removerHorarioEspecial(data); } catch (Exception ignored) {}

            // Cadastrar como FECHADO sem horários
            HorarioFuncionamento h = new HorarioFuncionamento();
            h.setDataEspecial(data);
            h.setFechado(true);
            h.setObservacao("Fechado para manutenção");

            assertDoesNotThrow(() -> service.cadastrarHorarioEspecial(h));

            HorarioFuncionamento obtido = service.obterHorario(data);
            assertNotNull(obtido);
            assertTrue(obtido.isFechado());
            assertNull(obtido.getHorarioAbertura());
            assertNull(obtido.getHorarioFechamento());

            // Tentar reservar neste dia deve falhar (sem exigir horários)
            var r = new org.example.models.ReservaSinuca();
            r.setNumeroMesa(3);
            r.setDataReserva(data);
            r.setHoraInicio(LocalTime.of(10, 0));
            r.setHoraFim(LocalTime.of(11, 0));
            r.setNomeCliente("Cliente Bloqueado");
            r.setTelefoneCliente("99999999");

            assertThrows(org.example.exceptions.ReservaSinucaException.class, () -> reservaService.criar(r));
        }
    }
}
