package com.cog.assignment.repository;

import com.cog.assignment.domain.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
}
