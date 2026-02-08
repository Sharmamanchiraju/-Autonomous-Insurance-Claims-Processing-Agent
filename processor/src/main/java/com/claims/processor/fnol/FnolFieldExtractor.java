package com.claims.processor.fnol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.claims.processor.model.ClaimData;

@Component
public class FnolFieldExtractor {

    public ClaimData extract(String text) {

        if (text == null || text.isBlank()) {
            return ClaimData.builder().build();
        }

        return ClaimData.builder()
                .policyNumber(extractText(text, "POLICY\\s+NUMBER"))
                .policyHolderName(extractText(text, "INSURED\\s+NAME|POLICYHOLDER"))
                .incidentDate(extractText(text, "DATE\\s+OF\\s+LOSS"))
                .incidentTime(extractText(text, "TIME\\s+OF\\s+LOSS"))
                .incidentLocation(extractText(text, "LOCATION\\s+OF\\s+LOSS"))
                .description(extractText(text, "DESCRIBE\\s+DAMAGE|DESCRIPTION"))
                .claimant(extractText(text, "CLAIMANT"))
                .contactDetails(extractText(text, "PHONE|EMAIL"))
                .assetType(extractText(text, "VEHICLE|ASSET\\s+TYPE"))
                .assetId(extractText(text, "VIN|ASSET\\s+ID"))
                .claimType(extractText(text, "CLAIM\\s+TYPE"))
                .estimatedDamage(extractAmount(text, "ESTIMATE\\s+AMOUNT"))
                .initialEstimate(extractAmount(text, "INITIAL\\s+ESTIMATE"))
                .build();
    }

    private String extractText(String text, String labelRegex) {

    Pattern pattern = Pattern.compile(
            labelRegex
                    + "\\s*:?\\s*"                      
                    + "(?:\\R+)?\\s*"                    
                    + "([^\\r\\n:]{3,})",                
            Pattern.CASE_INSENSITIVE);

    Matcher matcher = pattern.matcher(text);

    if (!matcher.find()) {
        return null;
    }

    String value = matcher.group(1);

    if (value == null) {
        return null;
    }

    return value.replaceAll("\\s{2,}", " ").trim();
}

    private Double extractAmount(String text, String labelRegex) {

        Pattern pattern = Pattern.compile(
                labelRegex + "\\s*:?\\s*(\\d+(?:\\.\\d+)?)",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(text);

        return matcher.find()
                ? Double.valueOf(matcher.group(1))
                : null;
    }
}
