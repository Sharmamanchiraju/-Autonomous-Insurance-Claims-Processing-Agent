package com.claims.processor.routing;

import java.util.List;

import org.springframework.stereotype.Component;

import com.claims.processor.model.ClaimData;
import com.claims.processor.model.ClaimRoute;

@Component
public class ClaimRouter {

    public ClaimRoute route(ClaimData claimData, List<String> missingFields) {

        // Rule 1: Missing mandatory fields → Manual Review
        if (!missingFields.isEmpty()) {
            return ClaimRoute.MANUAL_REVIEW;
        }

        // Rule 2: Fraud / Investigation keywords
        if (containsInvestigationKeywords(claimData.getDescription())) {
            return ClaimRoute.INVESTIGATION;
        }

        // Rule 3: Injury claims
        if ("injury".equalsIgnoreCase(claimData.getClaimType())) {
            return ClaimRoute.SPECIALIST_QUEUE;
        }

        // Rule 4: Fast-track for low damage
        if (claimData.getEstimatedDamage() != null
                && claimData.getEstimatedDamage() < 25000) {
            return ClaimRoute.FAST_TRACK;
        }

        // Default
        return ClaimRoute.MANUAL_REVIEW;
    }

    public String buildReasoning(
            ClaimRoute route,
            List<String> missingFields,
            ClaimData claimData) {

        switch (route) {

            case MANUAL_REVIEW:
                return missingFields.isEmpty()
                        ? "Claim requires manual review based on business rules"
                        : "Mandatory fields are missing: " + String.join(", ", missingFields);

            case INVESTIGATION:
                return "Claim description contains indicators requiring investigation";

            case SPECIALIST_QUEUE:
                return "Claim type is injury and requires specialist handling";

            case FAST_TRACK:
                return "Estimated damage is below 25,000 and all mandatory fields are present";

            default:
                return "Claim routed based on standard processing rules";
        }
    }

    private boolean containsInvestigationKeywords(String description) {

        if (description == null) {
            return false;
        }

        String text = description.toLowerCase();

        return text.contains("fraud")
                || text.contains("inconsistent")
                || text.contains("staged");
    }
}
