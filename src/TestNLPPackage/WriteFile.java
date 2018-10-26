package TestNLPPackage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WriteFile {

    /**
     * This class shows how to write file in java
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        String data = "I will write this String to File in Java";
        int noOfLines = 1;
        writeUsingFileWriter(data);

        writeUsingBufferedWriter(data, noOfLines);

        writeUsingFiles(data);

        writeUsingOutputStream(data);
        System.out.println("DONE");
    }

    /**
     * Use Streams when you are dealing with raw data
     * @param data
     */
    private static void writeUsingOutputStream(String data) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/TetsingOutputStream"));
            os.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use Files class from Java 1.7 to write files, internally uses OutputStream
     * @param data
     */
    private static void writeUsingFiles(String data) {
        try {
            Files.write(Paths.get("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/TestingWriteUsingFiles"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use BufferedWriter when number of write operations are more
     * It uses internal buffer to reduce real IO operations and saves time
     * @param data
     * @param noOfLines
     */
    private static void writeUsingBufferedWriter(String data, int noOfLines) {
        File file = new File("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/TestingBufferredWriter");
        FileWriter fr = null;
        BufferedWriter br = null;
        String dataWithNewLine=data;
        try{
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for(int i = noOfLines; i>0; i--){
                br.write(dataWithNewLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use FileWriter when number of write operations are less
     * @param data
     */
    private static void writeUsingFileWriter(String data) {
        File file = new File("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/TestingFileWriter");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
