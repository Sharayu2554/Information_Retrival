package DataModels;

import java.io.Serializable;

public class CompressedDocumentDataInt implements Serializable {
    int maxTf;
    int docLen;

    public CompressedDocumentDataInt(int maxTf, int docLen) {
        this.maxTf = maxTf;
        this.docLen = docLen;
    }
}
