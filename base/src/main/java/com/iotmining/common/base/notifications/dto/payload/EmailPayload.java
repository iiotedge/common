package com.iotmining.common.base.notifications.dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty; // Import this
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayload {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;

    @JsonProperty("isHtml")
    private boolean isHtml;
}