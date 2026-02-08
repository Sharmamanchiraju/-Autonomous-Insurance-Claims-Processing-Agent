package com.claims.processor.ingestion;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.claims.processor.model.RawDocumentData;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;

@Component
@RequiredArgsConstructor
public class TextractFormReader implements DocumentReader {
    private final TextractClient textractClient;

    @Override
    public RawDocumentData read(MultipartFile file) {

        try {
            ByteBuffer buffer = ByteBuffer.wrap(file.getBytes());

            AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                    .document(d -> d.bytes(SdkBytes.fromByteBuffer(buffer)))
                    .featureTypesWithStrings("FORMS", "TABLES")
                    .build();

            AnalyzeDocumentResponse response = textractClient.analyzeDocument(request);

            Map<String, String> keyValues = extractKeyValuePairs(response.blocks());

            return new RawDocumentData(keyValues);

        } catch (Exception ex) {
            return new RawDocumentData(Map.of());
        }
    }

    private Map<String, String> extractKeyValuePairs(List<Block> blocks) {

        Map<String, Block> blockMap = new HashMap<>();
        Map<String, Block> keyBlocks = new HashMap<>();
        Map<String, Block> valueBlocks = new HashMap<>();

        for (Block block : blocks) {
            blockMap.put(block.id(), block);

            if (block.blockType() == BlockType.KEY_VALUE_SET) {
                if (block.entityTypes().contains("KEY")) {
                    keyBlocks.put(block.id(), block);
                } else {
                    valueBlocks.put(block.id(), block);
                }
            }
        }

        Map<String, String> extracted = new HashMap<>();

        for (Block keyBlock : keyBlocks.values()) {
            Block valueBlock = findValueBlock(keyBlock, valueBlocks);

            String key = extractText(keyBlock, blockMap);
            String value = valueBlock != null
                    ? extractText(valueBlock, blockMap)
                    : null;

            if (!key.isBlank() && value != null && !value.isBlank()) {
                extracted.put(key, value);
            }
        }

        return extracted;
    }

    private Block findValueBlock(Block keyBlock, Map<String, Block> valueBlocks) {

        if (keyBlock.relationships() == null) {
            return null;
        }

        for (Relationship rel : keyBlock.relationships()) {
            if (rel.type() == RelationshipType.VALUE) {
                for (String id : rel.ids()) {
                    return valueBlocks.get(id);
                }
            }
        }
        return null;
    }

    private String extractText(Block block, Map<String, Block> blockMap) {

        if (block.relationships() == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        for (Relationship rel : block.relationships()) {
            if (rel.type() == RelationshipType.CHILD) {
                for (String id : rel.ids()) {
                    Block word = blockMap.get(id);
                    if (word.blockType() == BlockType.WORD) {
                        text.append(word.text()).append(" ");
                    }
                }
            }
        }
        return text.toString().trim();
    }
}
