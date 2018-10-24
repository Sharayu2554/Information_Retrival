import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Indexing {

    private static final String PATTERN = "<.*>";
////    HashMap<String>
//    Byt

    public static void getInvertedIndex(String[] data) {

    }

    public static void getTextFromFile(String fileName) throws Exception {
        String xml = new String(Files.readAllBytes(Paths.get(fileName)));
        xml = xml.replaceAll(Indexing.PATTERN, "");
//        String[] data =
//                xml.replaceAll("\'", "").replaceAll("\\)", "").
//                        replaceAll("\\(", "").replaceAll(",", " ").
//                        replaceAll(";", " ").replaceAll("\\\\", "").
//                        replaceAll("/", "").replaceAll("\n", " ").split("\\s+");
//        System.out.println(Arrays.toString(data));

        String[] data = xml.split("\\s+");
        System.out.println(Arrays.toString(data));
    }

    public static void processFilesFromFolder(File folder, String folderName) throws Exception {
        for (final File fileEntry : folder.listFiles()) {
            getTextFromFile(folderName + fileEntry.getName());
        }
    }

    public static void main(String args[]) throws Exception{

        if (args.length < 1) {
            System.out.println("Incorrect Parameters ");
            System.exit(-1);
        }
        processFilesFromFolder(new File(args[0]), args[0] + '/');
    }
}
