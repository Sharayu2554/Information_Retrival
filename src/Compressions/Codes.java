package Compressions;

import java.math.BigInteger;

public class Codes {

    public static String unaryCode(int number) {
        String unary = "";
        for (int i = 0; i< number; i++) {
            unary += "1";
        }
        unary += "0";
        return unary;
    }

    public static String gammaCodeString(int number) {

        String binary = Integer.toBinaryString(number);
        if (binary.length() > 1)  {
            String unaryOfOffset = unaryCode(binary.length() -1);
            return unaryOfOffset + binary.substring(1, binary.length());
        }
        return "0";
    }

    public static byte[] gammaCode(int number) {
        byte[] data = new BigInteger(gammaCodeString(number), 2).toByteArray();
        System.out.println(" byte length for number " + number + " is " + data.length );
        return data;
    }

    public static String deltaCodeString(int number) {
        String binary = Integer.toBinaryString(number);
        String gammaOfOffset = gammaCodeString(binary.length());
//        System.out.println(gammaOfOffset + binary.substring(1, binary.length()));
        return gammaOfOffset + binary.substring(1, binary.length());
    }

    public static byte[] deltaCode(int number) {
        return new BigInteger(deltaCodeString(number), 2).toByteArray();
    }

    public static void main(String[] args)
    {

        System.out.println("Hello World!");
        System.out.println("Gamma COde " + gammaCode(12345678));

        System.out.println(" Gamma Code of 2: " + gammaCode(2));
        System.out.println(" Gamma Code of 9 : " + gammaCode(9));
        System.out.println(" Gamma Code of 13: " + gammaCode(13));
        System.out.println(" Gamma Code of 19080: " + gammaCode(1400));

        System.out.println(" Delta code 1 : " + deltaCode(1));
        System.out.println(" Delta code 2 : " + deltaCode(2));
        System.out.println(" Delta code 9 : " + deltaCode(9));
        System.out.println(" Delta code 13 : " + deltaCode(13));
    }
}
