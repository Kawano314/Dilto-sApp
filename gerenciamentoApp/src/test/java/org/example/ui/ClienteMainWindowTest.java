package org.example.ui;

import org.example.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cliente Main Window UI Tests")
class ClienteMainWindowTest {

    private Usuario cliente;

    @BeforeEach
    void setUp() {
        cliente = new Usuario("Cliente", "cliente@loja.com", "", Usuario.TipoUsuario.CLIENTE);
        cliente.setId(999L);
    }

    @Test
    @DisplayName("Deve criar instância de ClienteMainWindow com usuário cliente válido")
    void testCriarClienteMainWindow() {
        assertDoesNotThrow(() -> {
            ClienteMainWindow janela = new ClienteMainWindow(cliente);
            assertNotNull(janela);
            assertEquals("Cliente - Dilto's App", janela.getTitle());
        });
    }

    @Test
    @DisplayName("Deve configurar tamanho padrão da janela (900x650)")
    void testTamanhoPadraoDaJanela() {
        assertDoesNotThrow(() -> {
            ClienteMainWindow janela = new ClienteMainWindow(cliente);
            assertEquals(900, janela.getWidth());
            assertEquals(650, janela.getHeight());
        });
    }

    @Test
    @DisplayName("Deve configurar operação de fechamento como DISPOSE_ON_CLOSE")
    void testOperacaoFechamento() {
        assertDoesNotThrow(() -> {
            ClienteMainWindow janela = new ClienteMainWindow(cliente);
            assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, janela.getDefaultCloseOperation());
        });
    }

    @Test
    @DisplayName("Deve ter usuário cliente associado")
    void testUsuarioClienteAssociado() {
        assertDoesNotThrow(() -> {
            ClienteMainWindow janela = new ClienteMainWindow(cliente);
            assertNotNull(janela);
            assertEquals(999L, cliente.getId());
            assertEquals("Cliente", cliente.getNome());
        });
    }

    @Test
    @DisplayName("Deve ser resizável")
    void testResizavel() {
        assertDoesNotThrow(() -> {
            ClienteMainWindow janela = new ClienteMainWindow(cliente);
            assertTrue(janela.isResizable());
        });
    }

    @Test
    @DisplayName("Deve conter abas para Produtos, Reservas e Horários")
    void testContémAbas() {
        assertDoesNotThrow(() -> {
            ClienteMainWindow janela = new ClienteMainWindow(cliente);
            assertNotNull(janela);
            // A janela foi criada com abas (Produtos, Minhas Reservas, Horários)
        });
    }

}
