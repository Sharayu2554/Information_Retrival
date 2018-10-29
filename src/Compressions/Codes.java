package Compressions;

import IRUtilies.ByteOperations;

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
        byte[] array = ByteOperations.StringCodesToByte(gammaCodeString(number));
        return array;
    }

    public static String deltaCodeString(int number) {
        String binary = Integer.toBinaryString(number);
        String gammaOfOffset = gammaCodeString(binary.length());
        return gammaOfOffset + binary.substring(1, binary.length());
    }

    public static byte[] deltaCode(int number)
    {
        byte array[] = ByteOperations.StringCodesToByte(deltaCodeString(number));
        return array;
    }

    public static void main(String[] args)
    {
        System.out.println("Hello World!");

        System.out.println(" Gamma Code of 1 : " + ByteOperations.ByteToStringCodes(gammaCode(1)));
        System.out.println(" Gamma Code of 2: " + ByteOperations.ByteToStringCodes(gammaCode(2)));
        System.out.println(" Gamma Code of 9 : " + ByteOperations.ByteToStringCodes(gammaCode(9)));
        System.out.println(" Gamma Code of 13: " + ByteOperations.ByteToStringCodes(gammaCode(13)));
        System.out.println(" Gamma Code of 1400: " + ByteOperations.ByteToStringCodes(gammaCode(1400)));

        System.out.println(" Delta code 1 : " + ByteOperations.ByteToStringCodes(deltaCode(1)));
        System.out.println(" Delta code 2 : " + ByteOperations.ByteToStringCodes(deltaCode(2)));
        System.out.println(" Delta code 9 : " + ByteOperations.ByteToStringCodes(deltaCode(9)));
        System.out.println(" Delta code 13 : " + ByteOperations.ByteToStringCodes(deltaCode(13)));

    }
}
