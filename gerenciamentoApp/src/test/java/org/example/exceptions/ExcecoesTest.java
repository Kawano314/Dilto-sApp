package org.example.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Exceções Customizadas")
class ExcecoesTest {

    @Test
    @DisplayName("Deve lançar ReservaSinucaException corretamente")
    void testReservaSinucaException() {
        String mensagem = "Erro na reserva";
        ReservaSinucaException e = assertThrows(
            ReservaSinucaException.class,
            () -> { throw new ReservaSinucaException(mensagem); }
        );
        assertEquals(mensagem, e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ProdutoException corretamente")
    void testProdutoException() {
        String mensagem = "Erro no produto";
        ProdutoException e = assertThrows(
            ProdutoException.class,
            () -> { throw new ProdutoException(mensagem); }
        );
        assertEquals(mensagem, e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar HorarioException corretamente")
    void testHorarioException() {
        String mensagem = "Erro no horário";
        HorarioException e = assertThrows(
            HorarioException.class,
            () -> { throw new HorarioException(mensagem); }
        );
        assertEquals(mensagem, e.getMessage());
    }
}
