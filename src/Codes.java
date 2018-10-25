import java.math.BigInteger;
import java.util.Arrays;
import java.lang.instrument.Instrumentation;


public class Codes {

    public static String unaryCode(int number) {
        String unary = "";
        for (int i = 0; i< number; i++) {
            unary += "1";
        }
        unary += "0";
        return unary;
    }


    public static String gammaCode(int number) {

        String binary = Integer.toBinaryString(number);
        if (binary.length() > 1)  {
            String unaryOfOffset = unaryCode(binary.length() -1);
            System.out.println("Unary COde " + unaryOfOffset + binary.substring(1, binary.length()));
            System.out.println("BigInt " + new BigInteger(unaryOfOffset + binary.substring(1, binary.length()), 2) );
            byte[] data = new BigInteger(unaryOfOffset + binary.substring(1, binary.length()), 2).toByteArray();
            System.out.println("data : "  + Arrays.toString(data) + " array size :" + data.length );

            System.out.println("data to bigint " + new BigInteger(1, data));
            return unaryOfOffset + binary.substring(1, binary.length());
        }
        return "0";
    }

    public static String deltaCode(int number) {
        String binary = Integer.toBinaryString(number);
        String gammaOfOffset = gammaCode(binary.length());
        return gammaOfOffset + binary.substring(1, binary.length());
    }

    public static void main(String[] args)
    {
        System.out.println("Hello World!");
        System.out.println("Unary COde " + gammaCode(12345678));

        System.out.println(" Gamma Code of 1: " + gammaCode(1));
        System.out.println(" Gamma Code of 2: " + gammaCode(2));
        System.out.println(" Gamma Code of 9 : " + gammaCode(9));
        System.out.println(" Gamma Code of 13: " + gammaCode(13));
        System.out.println(" Gamma Code of 13: " + gammaCode(19080));

        System.out.println(" Delta code 1 : " + deltaCode(1));
        System.out.println(" Delta code 2 : " + deltaCode(2));
        System.out.println(" Delta code 9 : " + deltaCode(9));
        System.out.println(" Delta code 13 : " + deltaCode(13));
    }
}
