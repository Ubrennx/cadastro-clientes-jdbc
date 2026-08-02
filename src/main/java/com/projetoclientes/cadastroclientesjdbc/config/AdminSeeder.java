package com.projetoclientes.cadastroclientesjdbc.config;

import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.enums.UsuarioRole;
import com.projetoclientes.cadastroclientesjdbc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminSenha;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       @Value("${admin.default.email}") String adminEmail,
                       @Value("${admin.default.senha}") String adminSenha) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminSenha = adminSenha;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean existeAdmin = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == UsuarioRole.ADMIN);

        if (!existeAdmin) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setRole(UsuarioRole.ADMIN);
            usuarioRepository.save(admin);

            System.out.println("=========================================");
            System.out.println("Usuário ADMIN padrão criado");
            System.out.println("email: " + adminEmail);
            System.out.println("senha: " + adminSenha);
            System.out.println("=========================================");
        }
    }
}
