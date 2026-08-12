package com.epr.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanCardResponse(
        Long id,
        String title,
        List<String> items,
        BigDecimal price
) {
}
