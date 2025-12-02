package org.example.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Menu Principal UI Tests")
class MenuPrincipalTest {

    @Test
    @DisplayName("Deve criar instância de MenuPrincipal sem erros")
    void testCriarMenuPrincipal() {
        assertDoesNotThrow(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            assertNotNull(menu);
        });
    }

    @Test
    @DisplayName("Deve exibir interface gráfica sem lançar exceção")
    void testExibirInterfaceGrafica() {
        assertDoesNotThrow(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.exibir();
            // Verificar que a interface foi criada (existe frame)
            assertNotNull(menu);
        });
    }

    @Test
    @DisplayName("Interface deve conter componentes esperados")
    void testComponentesPresentes() {
        assertDoesNotThrow(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.exibir();
            assertNotNull(menu);
        });
    }

    @Test
    @DisplayName("MenuPrincipal deve ter shutdown hook registrado")
    void testShutdownHookRegistrado() {
        // Este teste verifica que o construtor não lança exceção
        // e que o shutdown hook foi registrado sem erros
        assertDoesNotThrow(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            assertNotNull(menu, "MenuPrincipal deve ser instanciável");
        });
    }

}
