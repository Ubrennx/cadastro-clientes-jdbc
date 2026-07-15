package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entity.Cliente;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.exceptions.ResourceNotFoundException;
import com.luiz.cadastroclientes.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public Cliente insert(Cliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new DatabaseException("Já existe um cliente com esse email");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente findById(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElseThrow(() -> new ResourceNotFoundException(
                "entidade não encontrada com id: " + id));
    }

    public Cliente findByEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "entidade não encontrada com email: " + email));
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public void updateData(Cliente entidade, Cliente cliente) {
        entidade.setEmail(cliente.getEmail());
        entidade.setSenha(cliente.getSenha());
    }

    public Cliente update(Long id, Cliente cliente) {
        Cliente entidade = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("entidade não encontrada com id: " + id));
        updateData(entidade, cliente);
        return clienteRepository.update(entidade);
    }

    public void delete(Long id) {
        try {
            clienteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

}
