package com.claims.processor.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClaimResponse {

    private Map<String, Object> extractedFields;
    private List<String> missingFields;
    private ClaimRoute recommendedRoute;
    private String reasoning;
}
