package com.claims.processor.extraction.ocr;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.claims.processor.extraction.ExtractionResult;
import com.claims.processor.extraction.TextExtractor;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;

@Slf4j
@Component
@ConditionalOnProperty(name = "ocr.enabled", havingValue = "true")
public class OcrTextExtractor implements TextExtractor {

    @Override
    public ExtractionResult extract(MultipartFile file) {

        System.out.println(">>> OCR EXTRACTOR EXECUTED <<<");

        try {
            String text = performOcr(file);
            boolean success = text != null && text.length() > 200;

            log.info("OCR executed, success={}, textLength={}",
                    success, text == null ? 0 : text.length());

            return new ExtractionResult(text, success, name());

        } catch (Exception e) {
            log.error("OCR failed", e);
            return new ExtractionResult("", false, name());
        }
    }

    private String performOcr(MultipartFile file) throws Exception {

        StringBuilder extractedText = new StringBuilder();

        File tessDataDir = copyTessDataToTemp();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {

            PDFRenderer renderer = new PDFRenderer(document);

            Tesseract tesseract = new Tesseract();
            tesseract.setLanguage("eng");
            tesseract.setOcrEngineMode(1);
            tesseract.setPageSegMode(6);

            for (int page = 0; page < document.getNumberOfPages(); page++) {

                BufferedImage image = renderer.renderImageWithDPI(page, 400);

                BufferedImage gray = toGrayScale(image);
                BufferedImage processed = threshold(gray);

                extractedText.append(
                        tesseract.doOCR(processed)
                ).append("\n");
            }
        }

        return extractedText.toString().trim();
    }

    private File copyTessDataToTemp() throws IOException {

        File tempDir = Files.createTempDirectory("tessdata").toFile();
        tempDir.deleteOnExit();

        ClassPathResource resource =
                new ClassPathResource("tessdata/eng.traineddata");

        File trainedData = new File(tempDir, "eng.traineddata");

        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, trainedData.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return tempDir;
    }

    private BufferedImage toGrayScale(BufferedImage src) {
        BufferedImage gray = new BufferedImage(
                src.getWidth(),
                src.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = gray.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        return gray;
    }

    private BufferedImage threshold(BufferedImage image) {
        BufferedImage binary = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D g = binary.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return binary;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String name() {
        return "OCR";
    }
}
