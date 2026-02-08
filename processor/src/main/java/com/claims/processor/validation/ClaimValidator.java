package com.claims.processor.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.claims.processor.model.ClaimData;

@Component
public class ClaimValidator {
    public List<String> validate(ClaimData claimData) {

        List<String> missingFields = new ArrayList<>();

        // Policy Information
        checkMissing(claimData.getPolicyNumber(), "policyNumber", missingFields);
        checkMissing(claimData.getPolicyHolderName(), "policyHolderName", missingFields);
        checkMissing(claimData.getEffectiveDates(), "effectiveDates", missingFields);

        // Incident Information
        checkMissing(claimData.getIncidentDate(), "incidentDate", missingFields);
        checkMissing(claimData.getIncidentTime(), "incidentTime", missingFields);
        checkMissing(claimData.getIncidentLocation(), "incidentLocation", missingFields);
        checkMissing(claimData.getDescription(), "description", missingFields);

        // Involved Parties
        checkMissing(claimData.getClaimant(), "claimant", missingFields);
        checkMissing(claimData.getContactDetails(), "contactDetails", missingFields);

        // Asset Details
        checkMissing(claimData.getAssetType(), "assetType", missingFields);
        checkMissing(claimData.getAssetId(), "assetId", missingFields);
        checkMissing(claimData.getEstimatedDamage(), "estimatedDamage", missingFields);

        // Other Mandatory Fields
        checkMissing(claimData.getClaimType(), "claimType", missingFields);
        checkMissing(claimData.getAttatchments(), "attachments", missingFields);
        checkMissing(claimData.getInitialEstimate(), "initialEstimate", missingFields);

        return missingFields;
    }

    private void checkMissing(Object value, String fieldName, List<String> missingFields) {
        if (value == null) {
            missingFields.add(fieldName);
        }
    }
}
