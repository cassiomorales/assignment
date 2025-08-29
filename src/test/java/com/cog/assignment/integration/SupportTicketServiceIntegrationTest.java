package com.cog.assignment.integration;


import com.cog.assignment.domain.SupportTicket;
import com.cog.assignment.dto.EventType;
import com.cog.assignment.dto.SupportTicketEvent;
import com.cog.assignment.dto.SupportTicketRequest;
import com.cog.assignment.repository.SupportTicketRepository;
import com.cog.assignment.service.SupportTicketService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"ticket-events"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
class SupportTicketServiceIntegrationTest {

    @Autowired
    private SupportTicketService supportTicketService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Mock
    private SupportTicketRepository supportTicketRepository;

    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private SupportTicketRequest supportTicketRequest;
    private SupportTicket supportTicket;

    private BlockingQueue<String> kafkaMessages;

    @BeforeEach
    void setUp() {
        supportTicketRequest = new SupportTicketRequest("Title", "Description");
        supportTicket = new SupportTicket();
        kafkaMessages = new LinkedBlockingQueue<>();
        supportTicketRepository.deleteAll();
    }

    @KafkaListener(topics = "ticket-events")
    void listen(String message) {
        kafkaMessages.add(message);
    }

    @Test
    void saveSupportTicket_shouldSaveAndPublishEventToKafka_WhenSuccess() throws InterruptedException, JsonProcessingException {
        SupportTicketRequest request = new SupportTicketRequest();
        request.setTitle("Test Ticket");
        request.setDescription("Test Description");

        SupportTicket savedTicket = supportTicketService.saveSupportTicket(request);

        String kafkaMessage = kafkaMessages.poll(5, TimeUnit.SECONDS);
        SupportTicketEvent event = mapper.readValue(kafkaMessage, SupportTicketEvent.class);
        Assertions.assertEquals(EventType.CREATED, event.getEventType());
        Assertions.assertEquals(savedTicket.getId().toString(), event.getTicketId());
        Assertions.assertNotNull(event.getTimestamp());
    }
}
