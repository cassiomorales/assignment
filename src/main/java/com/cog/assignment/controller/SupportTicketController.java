package com.cog.assignment.controller;

import com.cog.assignment.dto.SupportTicketRequest;
import com.cog.assignment.dto.SupportTicketResponse;
import com.cog.assignment.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> saveSupportTicket(@RequestBody SupportTicketRequest request) {
        supportTicketService.saveSupportTicket(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupportTicketResponse> getSupportTicketById(@PathVariable(name = "id") String id) {
        return new ResponseEntity<>(supportTicketService.getSupportTicketById(id), HttpStatus.OK);
    }

}
