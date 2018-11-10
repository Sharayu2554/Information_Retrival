package src.DataModels;

public class PostingDoc {
    int docId;
    int tf;
    double wt;

    public PostingDoc(int docId, int tf) {
        this.docId = docId;
        this.tf = tf;
        this.wt = 0;
    }

    public PostingDoc(int docId, int tf, double wt) {
        this.docId = docId;
        this.tf = tf;
        this.wt = wt;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public double getWt() {
        return wt;
    }

    public void setWt(double wt) {
        this.wt = wt;
    }

    @Override
    public String toString() {
        return "PostingDoc{" +
                "docId=" + docId +
                ", tf=" + tf +
                ", wt=" + wt +
                '}';
    }
}
