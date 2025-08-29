package com.cog.assignment.controller;

import com.cog.assignment.domain.SupportTicket;
import com.cog.assignment.dto.SupportTicketRequest;
import com.cog.assignment.dto.SupportTicketResponse;
import com.cog.assignment.service.SupportTicketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SupportTicketControllerTest {

    @Mock
    private SupportTicketService supportTicketService;

    @InjectMocks
    private SupportTicketController supportTicketController;

    private SupportTicketRequest request;
    private SupportTicketResponse response;

    @BeforeEach
    void setUp() {
        request = new SupportTicketRequest();
        response = new SupportTicketResponse();
        response.setTitle("Test Ticket Title");
        response.setDescription("Test Ticket Description");
    }

    @Test
    void saveSupportTicket_ShouldReturnCreatedStatus_WhenRequestIsValid() {
        BDDMockito.when(supportTicketService.saveSupportTicket(BDDMockito.any(SupportTicketRequest.class))).thenReturn(
                new SupportTicket(request));

        ResponseEntity<HttpStatus> result = supportTicketController.saveSupportTicket(request);

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getSupportTicketById_ShouldReturnTicket_WhenIdExists() {
        String ticketId = "123";
        BDDMockito.when(supportTicketService.getSupportTicketById(ticketId)).thenReturn(response);

        ResponseEntity<SupportTicketResponse> result = supportTicketController.getSupportTicketById(ticketId);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(response, result.getBody());
    }

    @Test
    void getSupportTicketById_ShouldThrowException_WhenIdDoesNotExist() {
        String ticketId = "999";
        BDDMockito.when(supportTicketService.getSupportTicketById(ticketId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found " + ticketId));

        try {
            supportTicketController.getSupportTicketById(ticketId);
        } catch (RuntimeException e) {
            Assertions.assertEquals("404 NOT_FOUND \"Ticket not found 999\"", e.getMessage());
        }
    }
}
