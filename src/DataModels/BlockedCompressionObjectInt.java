package DataModels;

import java.io.Serializable;
import java.util.List;

public class BlockedCompressionObjectInt implements Serializable {
    int df;
    int tf;
    List<CompressedDocPostingInt> postingList;

    public BlockedCompressionObjectInt(int df, int tf, List<CompressedDocPostingInt> postingList) {
        this.df = df;
        this.tf = tf;
        this.postingList = postingList;
    }
}
