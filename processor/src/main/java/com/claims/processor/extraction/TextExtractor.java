package com.claims.processor.extraction;

import org.springframework.web.multipart.MultipartFile;

public interface TextExtractor {
 ExtractionResult extract(MultipartFile file);

    boolean isEnabled();

    String name();
}
