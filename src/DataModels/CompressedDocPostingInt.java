package DataModels;

import java.io.Serializable;

public class CompressedDocPostingInt implements Serializable {
    byte[] gap;
    CompressedDocumentDataInt documentData;

    public CompressedDocPostingInt(byte[] gap, CompressedDocumentDataInt documentData) {
        this.gap = gap;
        this.documentData = documentData;
    }
}
