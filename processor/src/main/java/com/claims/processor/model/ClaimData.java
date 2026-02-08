package com.claims.processor.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClaimData {

    // policy info
    private String policyNumber;
    private String policyHolderName;
    private String effectiveDates;

    // incident info
    private String incidentDate;
    private String incidentTime;
    private String incidentLocation;
    private String description;

    // Involved parties
    private String claimant;
    private String thirdParties;
    private String contactDetails;

    // Assest Details
    private String assetType;
    private String assetId;
    private Double estimatedDamage;

    // Other Mandatory Fields
    private String claimType;
    private String attatchments;
    private Double initialEstimate;

}
