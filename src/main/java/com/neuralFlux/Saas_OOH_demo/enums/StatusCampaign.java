package com.neuralFlux.Saas_OOH_demo.enums;

public enum StatusCampaign {
    PROPOSAL,      // 1. Proposta enviada
    NEGOTIATION,   // 2. Em negociação
    APPROVED,      // 3. Aprovado (mas ainda não agendado)
    RESERVED,      // 4. Reservado (Agendado no calendário)
    ACTIVE,        // 5. Ativo (A rodar no painel hoje)
    COMPLETED,     // 6. Finalizado (Data de término passou)
    LOST,          // 7. Perdido (Cliente não fechou)
    CANCELLED      // 8. Cancelado (Contrato rompido após o fechamento)
}