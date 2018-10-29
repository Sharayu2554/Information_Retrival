package DataModels;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public class CompressedDocumentData implements Serializable {
    byte[] maxTf;
    byte[] docLen;

    public CompressedDocumentData(byte[] maxTf, byte[] docLen) {
        this.maxTf = maxTf;
        this.docLen = docLen;
    }

    public byte[] getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(byte[] maxTf) {
        this.maxTf = maxTf;
    }

    public byte[] getDocLen() {
        return docLen;
    }

    public void setDocLen(byte[] docLen) {
        this.docLen = docLen;
    }

    @Override
    public String toString() {
        return "{" +
                "maxTf=" + new BigInteger(maxTf) +
                ", docLen=" + new BigInteger(docLen) +
                '}';
    }
}
