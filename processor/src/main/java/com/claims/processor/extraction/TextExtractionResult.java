package com.claims.processor.extraction;

public class TextExtractionResult {
     private final String text;
    private final boolean success;

    public TextExtractionResult(String text, boolean success) {
        this.text = text;
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public boolean isSuccess() {
        return success;
    }
}
