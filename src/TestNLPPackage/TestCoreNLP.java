package TestNLPPackage;

import IRUtilies.Porter;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class TestCoreNLP {
    private static final String PATTERN = "<.*>";
////    HashMap<String>
//    Byt

    public static void getInvertedIndex(String[] data) {

    }

    /**
     * Read file
     * Read each line
     * for each line
     * tokenize the line
     * apply pos tagger to the array
     * Remove unwanted tags
     * lemmatize those words
     * remove punctuations
     * remove stop words
     * create dictionary
     * add to posting list
     * sort the dict
     * @param fileName
     * @throws Exception
     */
    public static void getTextFromFile(String fileName) throws Exception {
        String xml = new String(Files.readAllBytes(Paths.get(fileName)));
        xml = xml.replaceAll(TestCoreNLP.PATTERN, "");
//        String[] data =
//                xml.replaceAll("\'", "").replaceAll("\\)", "").
//                        replaceAll("\\(", "").replaceAll(",", " ").
//                        replaceAll(";", " ").replaceAll("\\\\", "").
//                        replaceAll("/", "").replaceAll("\n", " ").split("\\s+");
//        System.out.println(Arrays.toString(data));

//        String[] data = xml.split("\\s+");
//        System.out.println(Arrays.toString(data));

        PrintWriter out  = new PrintWriter(fileName+ "_out.txt");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        Annotation annotation;
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        annotation = new Annotation(xml);
        pipeline.annotate(annotation);
        System.out.println(annotation.keySet());
        System.out.println(annotation.get(CoreAnnotations.TokensAnnotation.class));

        Porter porter = new Porter();
        for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            System.out.println(token.word() + "\t" + token.lemma() + "\t" + token.tag() + "\t" + porter.stripAffixes(token.originalText()));
        }

        pipeline.prettyPrint(annotation, out);

    }

    public static void processFilesFromFolder(File folder, String folderName) throws Exception {
        for (final File fileEntry : folder.listFiles()) {
            getTextFromFile(folderName + fileEntry.getName());
        }
    }

    public static void main(String args[]) throws Exception {
        if (args.length < 1) {
            System.out.println("Incorrect Parameters ");
            System.exit(-1);
        }
        processFilesFromFolder(new File(args[0]), args[0] + '/');
    }
}
