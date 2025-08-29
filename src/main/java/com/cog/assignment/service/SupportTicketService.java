package com.cog.assignment.service;

import com.cog.assignment.domain.SupportTicket;
import com.cog.assignment.dto.EventType;
import com.cog.assignment.dto.SupportTicketEvent;
import com.cog.assignment.dto.SupportTicketRequest;
import com.cog.assignment.dto.SupportTicketResponse;
import com.cog.assignment.repository.SupportTicketRepository;
import com.cog.assignment.utils.JsonMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SupportTicketRepository supportTicketRepository;

    @Transactional
    public SupportTicket saveSupportTicket(SupportTicketRequest supportTicketRequest) {
        SupportTicket supportTicket = new SupportTicket(supportTicketRequest);
        supportTicket = supportTicketRepository.save(supportTicket);
        publishEvent(supportTicket);
        return supportTicket;
    }

    private static final String KAFKA_TOPIC = "ticket-events";
    private void publishEvent(SupportTicket ticket) {
        try {
            kafkaTemplate.send(KAFKA_TOPIC, JsonMapperUtil.mapToJsonString(new SupportTicketEvent(
                    EventType.CREATED, ticket.getId().toString(), OffsetDateTime.now().toString(),
                    JsonMapperUtil.mapToJsonString(ticket)
            )));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
        }
    }

    public SupportTicketResponse createResponseFromSupportTicketEntity(SupportTicket supportTicket) {
        return new SupportTicketResponse(supportTicket.getTitle(), supportTicket.getDescription());
    }

    public SupportTicketResponse getSupportTicketById(String id) {
        return createResponseFromSupportTicketEntity(supportTicketRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found " + id)));
    }
}
