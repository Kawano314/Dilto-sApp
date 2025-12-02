package org.example.models;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private Connection connection;

    public ConnectDB() {
        try {
            File projectRoot = detectProjectRoot();
            File dbFile = new File(projectRoot, "database.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            
            System.out.println("[DB] Conectando ao banco: " + dbFile.getAbsolutePath());
            connection = DriverManager.getConnection(url);
            System.out.println("[DB] Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private File detectProjectRoot() {
        try {
            // Obter a localização do arquivo .class compilado
            File classFile = new File(ConnectDB.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            
            File current = classFile;
            
            // Se estamos em target/classes, subir 2 níveis para chegar na raiz do projeto
            // Se estamos em um JAR (target/*.jar), subir 1 nível
            if (current.isDirectory()) {
                // Estamos em target/classes -> subir para target -> subir para projeto
                current = current.getParentFile(); // target
                if (current != null) {
                    current = current.getParentFile(); // projeto
                }
            } else {
                // Estamos em um JAR -> subir para target -> subir para projeto
                current = current.getParentFile(); // target
                if (current != null) {
                    current = current.getParentFile(); // projeto
                }
            }
            
            if (current != null && current.exists()) {
                System.out.println("[DB] Raiz do projeto detectada: " + current.getAbsolutePath());
                return current;
            }
        } catch (URISyntaxException e) {
            System.err.println("[DB] Erro ao detectar raiz do projeto: " + e.getMessage());
        }
        
        // Fallback: usar diretório de trabalho atual
        File fallback = new File(System.getProperty("user.dir"));
        System.out.println("[DB] Usando diretório atual como fallback: " + fallback.getAbsolutePath());
        return fallback;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}