package DataModels;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public class CompressedDocPosting implements Serializable {
    byte[] gap;
    CompressedDocumentData documentData;

    public CompressedDocPosting(byte[] gap, CompressedDocumentData documentData) {
        this.gap = gap;
        this.documentData = documentData;
    }

    public byte[] getGap() {
        return gap;
    }

    public void setGap(byte[] gap) {
        this.gap = gap;
    }

    public CompressedDocumentData getDocumentData() {
        return documentData;
    }

    public void setDocumentData(CompressedDocumentData documentData) {
        this.documentData = documentData;
    }

    @Override
    public String toString() {
//        return "{" +
//                "gap=" + Arrays.toString(gap) +
//                ", documentData=" + documentData +
//                '}';
        return "{" +
                "gap=" + new BigInteger(gap) +
                ", documentData=" + documentData +
                '}';
    }
}
