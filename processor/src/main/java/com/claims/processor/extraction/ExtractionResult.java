package com.claims.processor.extraction;

public class ExtractionResult {
     private final String text;
    private final boolean success;
    private final String engine;

    public ExtractionResult(String text, boolean success, String engine) {
        this.text = text;
        this.success = success;
        this.engine = engine;
    }

    public String getText() {
        return text;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getEngine() {
        return engine;
    }
}
