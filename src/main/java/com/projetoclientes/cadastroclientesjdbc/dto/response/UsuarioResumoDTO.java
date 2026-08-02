package com.projetoclientes.cadastroclientesjdbc.dto.response;

import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;

public record UsuarioResumoDTO(Long id, String nome, String email) {
    public UsuarioResumoDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
