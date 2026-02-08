package com.claims.processor.agent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.claims.processor.extraction.BusinessFieldExtractor;
import com.claims.processor.extraction.TextExtractionService;
import com.claims.processor.fnol.FnolFieldExtractor;
import com.claims.processor.ingestion.DocumentReader;
import com.claims.processor.model.ClaimData;
import com.claims.processor.model.ClaimResponse;
import com.claims.processor.model.ClaimRoute;
import com.claims.processor.routing.ClaimRouter;
import com.claims.processor.validation.ClaimValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClaimProcessingAgent {

    private final ClaimValidator claimValidator;
    private final ClaimRouter claimRouter;
    private final TextExtractionService textExtractionService;
    private final FnolFieldExtractor fnolFieldExtractor;

    public ClaimResponse process(MultipartFile file) {

    String extractedText = textExtractionService.extractText(file);

    ClaimData claimData = fnolFieldExtractor.extract(extractedText);

    List<String> missing = claimValidator.validate(claimData);

    ClaimRoute route = claimRouter.route(claimData, missing);

    String reasoning = claimRouter.buildReasoning(route, missing, claimData);

    Map<String, Object> extractedFields = buildExtractedFieldsMap(claimData);

    return ClaimResponse.builder()
            .extractedFields(extractedFields)
            .missingFields(missing)
            .recommendedRoute(route)
            .reasoning(reasoning)
            .build();
}



    private Map<String, Object> buildExtractedFieldsMap(ClaimData claimData) {

        Map<String, Object> fields = new LinkedHashMap<>();

        // Policy Information
        putIfNotNull(fields, "policyNumber", claimData.getPolicyNumber());
        putIfNotNull(fields, "policyHolderName", claimData.getPolicyHolderName());
        putIfNotNull(fields, "effectiveDates", claimData.getEffectiveDates());

        // Incident Information
        putIfNotNull(fields, "incidentDate", claimData.getIncidentDate());
        putIfNotNull(fields, "incidentTime", claimData.getIncidentTime());
        putIfNotNull(fields, "incidentLocation", claimData.getIncidentLocation());
        putIfNotNull(fields, "description", claimData.getDescription());

        // Involved Parties
        putIfNotNull(fields, "claimant", claimData.getClaimant());
        putIfNotNull(fields, "thirdParties", claimData.getThirdParties());
        putIfNotNull(fields, "contactDetails", claimData.getContactDetails());

        // Asset Details
        putIfNotNull(fields, "assetType", claimData.getAssetType());
        putIfNotNull(fields, "assetId", claimData.getAssetId());
        putIfNotNull(fields, "estimatedDamage", claimData.getEstimatedDamage());

        // Other Mandatory Fields
        putIfNotNull(fields, "claimType", claimData.getClaimType());
        putIfNotNull(fields, "attachments", claimData.getAttatchments()); 
        putIfNotNull(fields, "initialEstimate", claimData.getInitialEstimate());

        return fields;
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
