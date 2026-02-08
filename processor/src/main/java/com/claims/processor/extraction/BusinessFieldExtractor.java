package com.claims.processor.extraction;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.claims.processor.model.ClaimData;
import com.claims.processor.model.RawDocumentData;

@Component
public class BusinessFieldExtractor {
    public ClaimData extract(RawDocumentData rawData) {

        Map<String, String> fields = rawData.getFields();

        return ClaimData.builder()
                // Policy Information
                .policyNumber(findValue(fields, "policy"))
                .policyHolderName(findValue(fields, "insured"))
                .effectiveDates(findValue(fields, "effective"))

                // Incident Information
                .incidentDate(findValue(fields, "date of loss"))
                .incidentTime(findValue(fields, "time"))
                .incidentLocation(findValue(fields, "location"))
                .description(findValue(fields, "description"))

                // Involved Parties
                .claimant(findValue(fields, "claimant"))
                .thirdParties(findValue(fields, "third"))
                .contactDetails(findValue(fields, "phone", "email"))

                // Asset Details
                .assetType(findValue(fields, "vehicle", "asset"))
                .assetId(findValue(fields, "vin", "asset id"))
                .estimatedDamage(parseDouble(findValue(fields, "estimate", "damage")))

                // Other Mandatory Fields
                .claimType(findValue(fields, "claim type", "line of business"))
                .attatchments(findValue(fields, "attachment"))
                .initialEstimate(parseDouble(findValue(fields, "initial estimate")))
                .build();
    }

    private String findValue(Map<String, String> fields, String... hints) {

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey().toLowerCase();

            for (String hint : hints) {
                if (key.contains(hint.toLowerCase())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private Double parseDouble(String value) {
    if (value == null || value.isBlank()) {
        return null; 
    }
    String cleaned = value.replaceAll("[^0-9.]", "");
    return cleaned.isBlank() ? null : Double.parseDouble(cleaned);
}

}
