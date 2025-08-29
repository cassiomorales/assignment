package com.cog.assignment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketRequest {

    @NonNull
    private String title;

    @NonNull
    private String description;

}
