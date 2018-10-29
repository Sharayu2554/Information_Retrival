package DataModels;

import java.io.Serializable;
import java.util.Arrays;

public class CompressedDocPosting implements Serializable {
    byte[] gap;
    int docId;

    public CompressedDocPosting(byte[] gap, int docId) {
        this.gap = gap;
        this.docId = docId;
    }

    public byte[] getGap() {
        return gap;
    }

    public void setGap(byte[] gap) {
        this.gap = gap;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    @Override
    public String toString() {
        return "CompressedDocPosting{" +
                "gap=" + Arrays.toString(gap) +
                ", docId=" + docId +
                '}';
    }
}
