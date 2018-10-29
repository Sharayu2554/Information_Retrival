package TestNLPPackage;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import IRUtilies.ByteOperations;
import com.sun.org.apache.bcel.internal.classfile.Code;

public class TestFileIO {

    private static BitSet fromString(String binary) {
        BitSet bitset = new BitSet(binary.length());
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                bitset.set(i);
            }
        }
        return bitset;
    }

    public static void main(String args[])throws Exception
    {
        FileOutputStream fout = new FileOutputStream("f1.txt");
        BufferedOutputStream bout = new BufferedOutputStream(fout);

        bout.write(ByteOperations.StringToByte("sharayu"));
        bout.write(ByteOperations.IntToByte(20));
        bout.write(ByteOperations.IntToByte(105));


        bout.write(ByteOperations.StringToByte("sharayu1"));
        bout.write(ByteOperations.IntToByte(21));
        bout.write(ByteOperations.IntToByte(106));


        bout.write(ByteOperations.StringToByte("sharayu2"));
        bout.write(ByteOperations.IntToByte(22));
        bout.write(ByteOperations.IntToByte(107));
        //illustrating close() method
        bout.close();
        fout.close();


        FileInputStream fin = new FileInputStream("f1.txt");
        BufferedInputStream bis = new BufferedInputStream(fin);

        while (bis.available() >= 28) {
            byte[] term = new byte[20];
            byte[] maxtf = new byte[4];
            byte[] docLen = new byte[4];

            bis.read(term, 0, 20);
            bis.read(maxtf, 0, 4);
            bis.read(docLen, 0, 4);

//            System.out.println(ByteOperations.ByteToString(term));
//            System.out.println(ByteOperations.ByteToInteger(maxtf));
//            System.out.println(ByteOperations.ByteToInteger(docLen));
        }

        bis.close();
        fin.close();

        //string
        System.out.println(Arrays.toString(fromString("10011010011010000111111010101010").toByteArray()));
        byte[] data = fromString("10011010011010000111111010101010").toByteArray();
        BitSet b = BitSet.valueOf(data);
        System.out.println(ByteOperations.ByteToString(data));
        //need to write to string function

//        System.out.println();

        //length
//        byte[] len = BitSet.valueOf(new long[] {200}).toByteArray();
//        System.out.println(Arrays.toString(len) + " " + len.length);
//        System.out.println(" 200 " + ByteOperations.ByteToInteger(len));
        //
    }
}
