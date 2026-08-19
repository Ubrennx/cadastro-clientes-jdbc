package com.projetoclientes.cadastroclientesjdbc;

import com.projetoclientes.cadastroclientesjdbc.dto.response.CompraResponseDTO;
import com.projetoclientes.cadastroclientesjdbc.service.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoService(rabbitTemplate);
        ReflectionTestUtils.setField(notificacaoService, "exchange", "notificacao.exchange");
        ReflectionTestUtils.setField(notificacaoService, "routingKey", "notificacao.compra");
        ReflectionTestUtils.setField(notificacaoService, "adminEmail", "admin@email.com");
    }

    @Test
    @DisplayName("notificarCompra deve publicar o payload correto na exchange/routing key configurados")
    void notificarCompraDevePublicarPayloadCorreto() {
        CompraResponseDTO compraDTO = new CompraResponseDTO(1L, null, 100.0, null, null);

        notificacaoService.notificarCompra(compraDTO);

        ArgumentCaptor<NotificacaoService.NotificacaoCompraPayload> payloadCaptor =
                ArgumentCaptor.forClass(NotificacaoService.NotificacaoCompraPayload.class);

        verify(rabbitTemplate).convertAndSend(
                eq("notificacao.exchange"),
                eq("notificacao.compra"),
                payloadCaptor.capture());

        NotificacaoService.NotificacaoCompraPayload payloadEnviado = payloadCaptor.getValue();

        assertThat(payloadEnviado).isNotNull();
        assertThat(payloadEnviado.compra()).isEqualTo(compraDTO);
        assertThat(payloadEnviado.adminEmail()).isEqualTo("admin@email.com");
    }
}
