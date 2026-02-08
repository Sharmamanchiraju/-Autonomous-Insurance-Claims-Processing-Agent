🧾 Autonomous Insurance Claims Processing Agent
📌 Problem Statement

Build a lightweight autonomous agent that:

Extracts key fields from FNOL (First Notice of Loss) documents

Identifies missing or inconsistent information

Routes the claim to the correct workflow

Explains the routing decision

The input documents are insurance FNOL PDFs (ACORD format) which may be scanned or digitally generated.

🏗️ Solution Overview

This project implements a production-style FNOL processing pipeline using Spring Boot with a hybrid text extraction strategy.

Key characteristics

No hardcoded values

Deterministic and explainable logic

Fault-tolerant extraction

Clear separation of responsibilities

🧠 High-Level Architecture
PDF Upload
   │
   ▼
Text Extraction Service
   ├── AWS Textract (Primary – structured forms)
   └── OCR (Fallback – scanned PDFs)
           │
           ▼
FNOL Field Extractor (Label + Regex based)
           │
           ▼
Claim Validator
           │
           ▼
Claim Router
           │
           ▼
JSON Response

🔍 Text Extraction Strategy
1️⃣ AWS Textract (Primary)

Uses Textract FORMS feature

Best for digitally generated PDFs

Extracts key–value pairs

Fast and highly accurate for structured fields

2️⃣ OCR (Fallback)

Uses Tesseract OCR

Handles scanned or image-based PDFs

Produces raw text

Activated automatically if Textract fails

The engine used is logged for every request:

Text extraction SUCCESS using engine=OCR
Text extraction SUCCESS using engine=AWS_TEXTRACT

🧠 FNOL Field Extraction Approach

OCR output is raw and noisy, especially for ACORD FNOL documents that contain:

Legal disclaimers

Repeated labels

Multi-column layouts

To handle this reliably, the system uses:

Label-anchored, field-specific regex extraction

Why this approach?

Avoids legal boilerplate text

Prevents greedy matches

Ensures deterministic results

Commonly used in real insurance systems

📋 Fields Extracted
Policy Information

Policy Number

Policyholder Name

Effective Dates

Incident Information

Date of Loss

Time of Loss

Location

Description

Involved Parties

Claimant

Third Parties

Contact Details

Asset Details

Asset Type

Asset ID

Estimated Damage

Other Mandatory Fields

Claim Type

Attachments

Initial Estimate

🔄 Claim Routing Rules
Condition	Route
Any mandatory field missing	MANUAL_REVIEW
Estimated damage < ₹25,000	FAST_TRACK
Description contains fraud keywords	INVESTIGATION
Claim type = Injury	SPECIALIST_QUEUE

Routing decisions always include a clear explanation.

📤 API Endpoint
Process Claim
POST /api/claims/process


Consumes

multipart/form-data


Request Parameter

file : FNOL PDF

📦 Sample Response
{
  "extractedFields": {
    "policyNumber": "POL-987654",
    "incidentDate": "12/02/2024",
    "incidentLocation": "HYDERABAD, TELANGANA",
    "description": "Rear bumper damaged due to collision",
    "estimatedDamage": 25000
  },
  "missingFields": [
    "policyHolderName",
    "incidentTime",
    "assetId"
  ],
  "recommendedRoute": "MANUAL_REVIEW",
  "reasoning": "Mandatory fields are missing: policyHolderName, incidentTime, assetId"
}

⚙️ Configuration
application.yml
ocr:
  enabled: true
  language: eng

aws:
  textract:
    enabled: true

extraction:
  min-text-length: 200

🛠️ Local Setup Instructions
1️⃣ Prerequisites

Java 17+

Maven

(Optional) AWS credentials for Textract

2️⃣ Install Tesseract (Required for OCR)

Download official Windows build:

https://github.com/UB-Mannheim/tesseract/wiki


Install with:

✔ Add to PATH

✔ English language selected

Verify installation:

tesseract --version
tesseract --list-langs


You must see:

eng

3️⃣ Environment Variables (System Variables)
TESSDATA_PREFIX=C:\Program Files\Tesseract-OCR\tessdata


Ensure PATH contains:

C:\Program Files\Tesseract-OCR


Restart IDE after setting variables.

4️⃣ Run the Application
mvn spring-boot:run

🧩 Design Decisions

OCR and Textract are decoupled

No guessing or default values

Missing data is routed intentionally

Validation and routing are separate layers

All decisions are explainable

This mirrors real-world FNOL automation systems.

🚀 Future Enhancements

Bounding-box based extraction (Textract geometry)

Field-level confidence scoring

Table extraction support

Multi-language OCR

🏁 Conclusion

This project demonstrates:

Robust document ingestion

Hybrid extraction strategy

Production-safe error handling

Deterministic claim routing

Clear, auditable decision logic

It closely reflects enterprise insurance FNOL processing pipelines.
