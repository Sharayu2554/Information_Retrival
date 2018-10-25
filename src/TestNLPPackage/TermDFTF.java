package TestNLPPackage;

public class TermDFTF {
    int df; //number of documents this term occurred
    int tf; //number of times that term occur in eachof the document

    public TermDFTF(int df, int tf) {
        this.df = df;
        this.tf = tf;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    @Override
    public String toString() {
        return "{ " +
                "df : " + df +
                ", tf : " + tf +
                " }";
    }
}
