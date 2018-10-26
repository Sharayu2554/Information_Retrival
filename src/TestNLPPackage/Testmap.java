package TestNLPPackage;

import Compressions.Codes;
import DataModels.CompressedDocumentData;
import DataModels.DocumentPosting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Testmap {

    public static void main(String args[]) {
        HashMap<Integer, DocumentPosting> data = new HashMap<>();
        DocumentPosting temp = new DocumentPosting(1, 2, 3);
        System.out.println(temp);

        data.put(1, temp);
        temp.setDocLen(4);

        System.out.println(temp);
        System.out.println(data.get(1));

        temp.setDocId(2);
        System.out.println(temp);
        System.out.println(data.get(1));
        data.put(1, temp);
        System.out.println(data.get(1));

        CompressedDocumentData comData = new CompressedDocumentData(Codes.gammaCode(100), Codes.gammaCode(200));

        try
        {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/SerializedTestCompressedDocumentObjectByte");
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(comData);
            out.close();
            file.close();
            System.out.println("Object has been serialized");
        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
    }
}
