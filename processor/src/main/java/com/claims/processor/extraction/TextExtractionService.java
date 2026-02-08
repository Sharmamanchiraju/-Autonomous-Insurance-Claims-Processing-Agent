package com.claims.processor.extraction;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextExtractionService {
    private final List<TextExtractor> extractors;
    private static final Logger log = LoggerFactory.getLogger(TextExtractionService.class);

    public TextExtractionService(List<TextExtractor> extractors) {
        this.extractors = extractors;
    }

    public String extractText(MultipartFile file) {

        for (TextExtractor extractor : extractors) {

            if (!extractor.isEnabled()) {
                continue;
            }

            ExtractionResult result = extractor.extract(file);
            log.info("Text extraction attempted using engine={}, success={}", result.getEngine(), result.isSuccess());
            if (result.isSuccess()) {
                log.info("Text extraction SUCCESS using engine={}", result.getEngine());

                return result.getText();
            }
        }
        log.warn("Text extraction FAILED using all engines");

        return "";
    }
}
