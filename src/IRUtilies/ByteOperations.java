package IRUtilies;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

public class ByteOperations {

    public static String ByteToStringCodes(byte[] input) {
        return new String(input);
    }

    public static byte[] StringToByte(String input) {
        return ByteBuffer.allocate(34).put(input.getBytes()).array();
    }

    public static byte[] IntToByte(Integer input) {
        return ByteBuffer.allocate(4).putInt(input).array();
    }

    public static Integer ByteToInteger(byte[] input) {
        return ByteBuffer.wrap(input).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public static String ByteToString(byte[] input) {
        int index = 0;
        while (index < input.length && input[index] != 0) {
            index++;
        }
        byte[] data = new byte[index];
        System.arraycopy(input, 0, data, 0, index);
        return new String(data);
    }

    public static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[(bits.length() + 7) / 8];
        for (int i=0; i<bits.length(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length-i/8-1] |= 1<<(i%8);
            }
        }
        return bytes;
    }

    public static byte[] StringCodesToByte(String binary) {
        if (binary.length() == 1) {
            if (binary.equals("0")) return new byte[] { 0b00 };
            if (binary.equals("1")) return new byte[] { 0b01 };
        }
        BitSet bitset = new BitSet(binary.length());
        int len = binary.length();
        for (int i = len-1; i >= 0; i--) {
            if (binary.charAt(i) == '1') {
                bitset.set(len-i-1);
            }
        }
        return bitset.toByteArray();
    }

    public static byte[] LenToByte(int len) {
        return StringCodesToByte(Integer.toBinaryString(len));
    }

    public static byte[] StringFlexToByte(String input) {
        return ByteBuffer.allocate(input.length()).put(input.getBytes()).array();
    }

    public static void main(String args[]) {
        System.out.println(ByteToString(StringToByte("data")));
        System.out.println(ByteToInteger(IntToByte(546745784)));
        byte[] len = LenToByte(10);
        System.out.println(Arrays.toString(len)+ " " + len.length);
        byte b = 0b00;
        System.out.println(b);
    }
}
