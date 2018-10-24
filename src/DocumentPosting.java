public class DocumentPosting {
    int docId;
    int maxTf; //frequencey of most frequent term or stem in that document
    int docLen; //total number of word occurrences in that document

    public DocumentPosting(int docId, int maxTf, int docLen) {
        this.docId = docId;
        this.maxTf = maxTf;
        this.docLen = docLen;
    }

    public int getDocId() {
        return docId;
    }

    public int getMaxTf() {
        return maxTf;
    }

    public int getDocLen() {
        return docLen;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

    public void setDocLen(int docLen) {
        this.docLen = docLen;
    }
}
