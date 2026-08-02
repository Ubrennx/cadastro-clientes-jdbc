package com.projetoclientes.cadastroclientesjdbc.resource;

import com.projetoclientes.cadastroclientesjdbc.dto.request.LoginRequestDTO;
import com.projetoclientes.cadastroclientesjdbc.dto.response.LoginResponseDTO;
import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthResource {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha()));

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = jwtService.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(
                usuario.getId(),
                token,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        ));
    }
}
