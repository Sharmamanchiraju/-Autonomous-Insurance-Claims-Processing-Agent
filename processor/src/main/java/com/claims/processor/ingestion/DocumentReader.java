package com.claims.processor.ingestion;

import org.springframework.web.multipart.MultipartFile;

import com.claims.processor.model.RawDocumentData;

public interface DocumentReader {
    
        RawDocumentData read(MultipartFile file);

}
