package com.claims.processor.extraction.aws;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.claims.processor.extraction.ExtractionResult;
import com.claims.processor.extraction.TextExtractor;

@Component
@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
public class AwsTextractTextExtractor implements TextExtractor {

    @Override
    public ExtractionResult extract(MultipartFile file) {
        try {
            String text = performTextract(file);
            boolean success = text != null && text.length() > 200;

            return new ExtractionResult(text, success, name());
        } catch (Exception e) {
            return new ExtractionResult("", false, name());
        }
    }

    private String performTextract(MultipartFile file) {
        return "...extracted text...";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String name() {
        return "AWS_TEXTRACT";
    }
}
