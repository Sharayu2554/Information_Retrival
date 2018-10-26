package DataModels;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TermPosting implements Serializable {
    int df; //number of documents this term occurred
    int tf; //number of times that term occur in eachof the document
    List<DocumentPosting> postingList; //list of documents containing the term

    public TermPosting(int df, int tf) {
        this.df = df;
        this.tf = tf;
        this.postingList = new LinkedList<>();
    }

    public TermPosting(int df, int tf, List<DocumentPosting> postingList) {
        this.df = df;
        this.tf = tf;
        this.postingList = postingList;
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

    public void setDf(int df) {
        this.df = df;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public void setPostingList(List<DocumentPosting> postingList)
    {
        this.postingList = postingList;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o != null) {
//            if (getClass() == o.getClass()) {
//                TermPosting that = (TermPosting) o;
//                return df == that.df &&
//                        tf == that.tf &&
//                        Objects.equals(term, that.term) &&
//                        Objects.equals(postingList, that.postingList);
//            }
//            else if (o.getClass() == term.getClass()) {
//                return o.equals(term);
//            }
//        }
//        return false;
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(term);
//    }

    @Override
    public String toString() {
        return "df : " + df +
                ", tf : " + tf +
                ", postingList : " + postingList;
    }
}
