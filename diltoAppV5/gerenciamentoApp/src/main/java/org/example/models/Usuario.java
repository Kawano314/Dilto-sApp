package org.example.models;

import java.time.LocalDateTime;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private LocalDateTime dataCadastro;
    private boolean ativo;

    public enum TipoUsuario {
        ADMIN("Administrador"),
        CLIENTE("Cliente");

        private final String descricao;

        TipoUsuario(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return String.format("Usuario [ID: %d | Nome: %s | Email: %s | Tipo: %s | Ativo: %s]",
                id, nome, email, tipo.getDescricao(), ativo ? "Sim" : "Não");
    }
}