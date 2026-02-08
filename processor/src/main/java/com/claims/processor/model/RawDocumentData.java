package com.claims.processor.model;

import java.util.Map;

public class RawDocumentData {

    private final Map<String, String> fields;

    public RawDocumentData(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
