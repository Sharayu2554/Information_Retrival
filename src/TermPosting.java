import java.util.LinkedList;
import java.util.List;

public class TermPosting {
    String term;
    int df; //number of documents this term occurred
    int tf; //number of times that term occur in eachof the document
    List<DocumentPosting> postingList; //list of documents containing the term

    public TermPosting(String term, int df, int tf) {
        this.term = term;
        this.df = df;
        this.tf = tf;
        this.postingList = new LinkedList<>();
    }

    public TermPosting(String term, int df, int tf, List<DocumentPosting> postingList) {
        this.term = term;
        this.df = df;
        this.tf = tf;
        this.postingList = postingList;
    }

    public String getTerm() {
        return term;
    }

    public int getDf() {
        return df;
    }

    public int getTf() {
        return tf;
    }

    public List<DocumentPosting> getPostingList() {
        return postingList;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public void setPostingList(List<DocumentPosting> postingList) {
        this.postingList = postingList;
    }
}
