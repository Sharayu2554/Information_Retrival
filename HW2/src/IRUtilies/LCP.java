package IRUtilies;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LCP {
    public static String longestCommonPrefix(String[] strs) {
        if(strs==null || strs.length==0){
            return "";
        }

        if(strs.length==1)
            return strs[0];

        int minLen = strs.length+1;

        for(String str: strs){
            if(minLen > str.length()){
                minLen = str.length();
            }
        }

        for(int i=0; i<minLen; i++){
            for(int j=0; j<strs.length-1; j++){
                String s1 = strs[j];
                String s2 = strs[j+1];
                if(s1.charAt(i)!=s2.charAt(i)){
                    return s1.substring(0, i);
                }
            }
        }

        return strs[0].substring(0, minLen);
    }

    public static String generateFrontCoding(String[] input) {
        String sub = longestCommonPrefix(input);
        String res = input[0].length() + sub + "*";
        boolean flag = true;
        for (String in : input) {
            String inSub = in.substring(sub.length(), in.length());
            if (flag) {
                res += inSub;
                flag = false;
                continue;
            }
            res += inSub.length() + "#" + inSub;
        }
        return res;
    }


    public static byte[] generateFrontCodedBytes(String[] input) {
        String sub = longestCommonPrefix(input);
        List<Byte> list = new LinkedList<Byte>();
        
        byte[] blen = ByteOperations.LenToByte(input[0].length());
        byte[] bsub = ByteOperations.StringFlexToByte(sub + "*");

        String res = input[0].length() + sub + "*";
        boolean flag = true;
        for (String in : input) {
            String inSub = in.substring(sub.length(), in.length());
            byte[] binSub = ByteOperations.StringFlexToByte(inSub);
            byte[] binSubToRes;
            if (flag) {
                binSubToRes = ByteOperations.StringFlexToByte(inSub);
                res += inSub;
                flag = false;
                continue;
            }
            byte[] binSubLen = ByteOperations.LenToByte(inSub.length());
            binSubToRes = ByteOperations.StringFlexToByte("#" + inSub);
            res += inSub.length() + "#" + inSub;
        }
        return res.getBytes();
    }

    public static void main(String args[]) {
        String[] inputs = new String[] { "ae", "aeaaaaaaa", "afater", "agree", "angle", "at" , "attack"};
        System.out.println(longestCommonPrefix(inputs));
        System.out.println(generateFrontCoding(inputs));
    }
}
