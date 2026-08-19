package com.projetoclientes.cadastroclientesjdbc.service;

import com.projetoclientes.cadastroclientesjdbc.dto.response.CompraResponseDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${admin.notificacao.email}")
    private String adminEmail;

    public NotificacaoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public record NotificacaoCompraPayload(CompraResponseDTO compra, String adminEmail) {}

    public void notificarCompra(CompraResponseDTO compra) {
        var payload = new NotificacaoCompraPayload(compra, adminEmail);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}