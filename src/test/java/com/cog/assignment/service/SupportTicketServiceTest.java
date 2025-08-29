package com.cog.assignment.service;

import com.cog.assignment.domain.SupportTicket;
import com.cog.assignment.dto.SupportTicketRequest;
import com.cog.assignment.dto.SupportTicketResponse;
import com.cog.assignment.repository.SupportTicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private SupportTicketService supportTicketService;

    private SupportTicketRequest supportTicketRequest;
    private SupportTicket supportTicket;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        supportTicketRequest = new SupportTicketRequest("Title", "Description");
        supportTicket = new SupportTicket(supportTicketRequest);
        supportTicket.setId(ticketId);
    }

    @Test
    void saveSupportTicket_shouldSaveAndPublishEvent() {
        BDDMockito.when(supportTicketRepository.save(BDDMockito.any(SupportTicket.class))).thenReturn(supportTicket);

        SupportTicket result = supportTicketService.saveSupportTicket(supportTicketRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(supportTicket.getDescription(), result.getDescription());
    }

    @Test
    void saveSupportTicket_shouldThrowExceptionOnJsonProcessingError() throws JsonProcessingException {
        BDDMockito.when(supportTicketRepository.save(BDDMockito.any(SupportTicket.class))).thenReturn(supportTicket);

        Assertions.assertThrows(ResponseStatusException.class, () ->
                supportTicketService.saveSupportTicket(supportTicketRequest));
    }

    @Test
    void getSupportTicketById_shouldReturnTicketIfExists() {
        BDDMockito.when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(supportTicket));

        SupportTicketResponse response = supportTicketService.getSupportTicketById(ticketId.toString());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(supportTicket.getTitle(), response.getTitle());
        Assertions.assertEquals(supportTicket.getDescription(), response.getDescription());
    }

    @Test
    void getSupportTicketById_shouldThrowExceptionIfTicketNotFound() {
        BDDMockito.when(supportTicketRepository.findById(BDDMockito.any(UUID.class))).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                supportTicketService.getSupportTicketById(UUID.randomUUID().toString()));
        Assertions.assertEquals("Ticket not found " + exception.getReason().split(" ")[3], exception.getReason());
    }
}
