package com.projetoclientes.cadastroclientesjdbc.dto.response;

public record LoginResponseDTO(Long id, String token, String tipo, String nome, String email, String role) {
    public LoginResponseDTO(Long id, String token, String nome, String email, String role) {
        this(id, token, "Bearer", nome, email, role);
    }
}
