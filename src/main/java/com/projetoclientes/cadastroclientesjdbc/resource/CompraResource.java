package com.projetoclientes.cadastroclientesjdbc.resource;

import com.projetoclientes.cadastroclientesjdbc.dto.response.CompraResponseDTO;
import com.projetoclientes.cadastroclientesjdbc.entities.Compra;
import com.projetoclientes.cadastroclientesjdbc.entities.Usuario;
import com.projetoclientes.cadastroclientesjdbc.enums.UsuarioRole;
import com.projetoclientes.cadastroclientesjdbc.service.CompraService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/compras")
@AllArgsConstructor
public class CompraResource {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraResponseDTO> insert(@RequestBody Compra compra) {
        Compra compraSalva = compraService.insert(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CompraResponseDTO(compraSalva));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> findAll(Authentication authentication) {
        Usuario logado = (Usuario) authentication.getPrincipal();

        if (logado.getRole() == UsuarioRole.ADMIN) {
            return ResponseEntity.ok(compraService.findAllDTO());
        }

        return ResponseEntity.ok(compraService.findByUsuarioDTO(logado.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> findById(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        Usuario logado = (Usuario) authentication.getPrincipal();
        CompraResponseDTO compra = compraService.findByIdDTO(id);

        boolean donoDaCompra = compra.usuario().id().equals(logado.getId());

        if (logado.getRole() != UsuarioRole.ADMIN && !donoDaCompra) {
            throw new AccessDeniedException("Você não tem permição para ver esta compra");
        }

        return ResponseEntity.ok(compra);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compra> update(@PathVariable Long id, @RequestBody Compra compra) {
        return ResponseEntity.ok(compraService.update(id, compra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
