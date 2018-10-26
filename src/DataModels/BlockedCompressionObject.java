package DataModels;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class BlockedCompressionObject implements Serializable {
    byte[] df;
    byte[] tf;
    List<CompressedDocPosting> postingList;

    public BlockedCompressionObject(byte[] df, byte[] tf, List<CompressedDocPosting> postingList) {
        this.df = df;
        this.tf = tf;
        this.postingList = postingList;
    }

    public byte[] getDf() {
        return df;
    }

    public void setDf(byte[] df) {
        this.df = df;
    }

    public byte[] getTf() {
        return tf;
    }

    public void setTf(byte[] tf) {
        this.tf = tf;
    }

    public List<CompressedDocPosting> getPostingList() {
        return postingList;
    }

    public void setPostingList(List<CompressedDocPosting> postingList) {
        this.postingList = postingList;
    }

    @Override
    public String toString() {
//        return "{" +
//                "df=" + Arrays.toString(df) +
//                ", tf=" + Arrays.toString(tf) +
//                ", postingList=" + postingList +
//                '}';
        return "{" +
                "df=" + new BigInteger(df) +
                ", tf=" + new BigInteger(tf) +
                ", postingList=" + postingList +
                '}';
    }
}