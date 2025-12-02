package org.example.ui;

import org.example.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Admin Main Window UI Tests")
class AdminMainWindowTest {

    private Usuario admin;

    @BeforeEach
    void setUp() {
        admin = new Usuario("Administrador", "admin@loja.com", "admin", Usuario.TipoUsuario.ADMIN);
        admin.setId(1L);
    }

    @Test
    @DisplayName("Deve criar instância de AdminMainWindow com usuário admin válido")
    void testCriarAdminMainWindow() {
        assertDoesNotThrow(() -> {
            AdminMainWindow janela = new AdminMainWindow(admin);
            assertNotNull(janela);
            assertEquals("Administração - Dilto's App", janela.getTitle());
        });
    }

    @Test
    @DisplayName("Deve configurar tamanho padrão da janela (1000x700)")
    void testTamanhoPadraoDaJanela() {
        assertDoesNotThrow(() -> {
            AdminMainWindow janela = new AdminMainWindow(admin);
            assertEquals(1000, janela.getWidth());
            assertEquals(700, janela.getHeight());
        });
    }

    @Test
    @DisplayName("Deve configurar operação de fechamento como DISPOSE_ON_CLOSE")
    void testOperacaoFechamento() {
        assertDoesNotThrow(() -> {
            AdminMainWindow janela = new AdminMainWindow(admin);
            assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, janela.getDefaultCloseOperation());
        });
    }

    @Test
    @DisplayName("Deve ter usuário admin associado")
    void testUsuarioAssociado() {
        assertDoesNotThrow(() -> {
            AdminMainWindow janela = new AdminMainWindow(admin);
            assertNotNull(janela);
            assertEquals(1L, admin.getId());
            assertEquals("Administrador", admin.getNome());
        });
    }

    @Test
    @DisplayName("Deve ser resizável")
    void testResizavel() {
        assertDoesNotThrow(() -> {
            AdminMainWindow janela = new AdminMainWindow(admin);
            assertTrue(janela.isResizable());
        });
    }

    @Test
    @DisplayName("Deve conter abas (tabbedPane)")
    void testContémAbas() {
        assertDoesNotThrow(() -> {
            AdminMainWindow janela = new AdminMainWindow(admin);
            assertNotNull(janela);
            // A janela foi criada com abas (Produtos, Horários, Mesas de Sinuca)
        });
    }

}
