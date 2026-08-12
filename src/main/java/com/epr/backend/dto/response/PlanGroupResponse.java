package com.epr.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanGroupResponse(
        Long id,
        String title,
        List<PlanCardResponse> cards,
        List<String> note
) {
}
