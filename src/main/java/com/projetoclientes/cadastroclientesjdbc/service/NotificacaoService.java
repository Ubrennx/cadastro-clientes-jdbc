package com.projetoclientes.cadastroclientesjdbc.service;

import com.projetoclientes.cadastroclientesjdbc.dto.response.CompraResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificacaoService {
    private RestTemplate restTemplate;

    public NotificacaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${n8n.webhook.url}")
    private String webhookUrl;

    @Value("${n8n.webhook.secret}")
    private String webhookSecret;

    @Value("${admin.notificacao.email}")
    private String adminEmail;

    public record NotificacaoCompraPayload(CompraResponseDTO compra, String adminEmail) {}

    public void notificarCompra(CompraResponseDTO compra) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Webhook-secret", webhookSecret);

        var payload = new NotificacaoCompraPayload(compra, adminEmail);
        HttpEntity<NotificacaoCompraPayload> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(webhookUrl, request, String.class);
    }

}
