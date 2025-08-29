package com.cog.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketEvent {
    private EventType eventType;
    private String ticketId;
    private String timestamp;
    private String payload;
}
