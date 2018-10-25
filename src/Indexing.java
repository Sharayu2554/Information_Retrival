import IRUtilies.Porter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Document;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Index;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexing {

    private static final String PATTERN = "<.*>";
    private static final String NUMBER_PATTERN = "\\d+";
    private static final Pattern DOCID_PATTERN = Pattern.compile("<DOCNO>(.*)</DOCNO>");
    private static final HashSet<String> stopWords = new HashSet<>();
    private static final HashSet<String> punctuations = new HashSet<>();
    private static Map<String, TreeSet<Integer>> lemmaDict = new TreeMap<>();
    private static Map<String, TreeSet<Integer>> stemmaDict = new TreeMap<>();
    private static Map<Integer, DocumentPosting> docLemmaPosting = new HashMap<>();
    private static Map<Integer, DocumentPosting> docStemmaPosting = new HashMap<>();

    public static void getInvertedIndex(String[] data) {

    }

    public static void printDictionary(Map<Integer, DocumentPosting> doc) {
        System.out.println("Doc Data : " );
        for (Integer docId : doc.keySet()) {
            System.out.println("Doc " + docId + " -->" + doc.get(docId).toString());
        }
    }

    public static void printMap(Map<String, TreeSet<Integer>> dict) {
        System.out.println("Dictionary Keys  : " + dict.size());
        for (String data: dict.keySet()) {
            System.out.println("--" + data + "--" + Arrays.toString(dict.get(data).toArray()));
        }
    }


    public static void getLemmaStemmaForDoc(String data, Integer docId) {

        //set properties to get tokens, split, pos tags and get its lemmas
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(data);
        pipeline.annotate(annotation);
        Porter porter = new Porter();
        Map<String, Integer> lemmas = new HashMap<>();
        Map<String, Integer> stemmas = new HashMap<>();
        int maxLemmaTf = 0;
        int maxStemmaTf = 0;
        for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            String stemm = porter.stripAffixes(token.originalText());
            if (!stopWords.contains(stemm) && !punctuations.contains(token.originalText())) {
//                System.out.println(token.word() + "\t" + token.lemma() +
//                        "\t" + porter.stripAffixes(token.originalText()));
                lemmas.put(token.lemma(), lemmas.getOrDefault(token.lemma(), 0) + 1);
                stemmas.put(stemm, stemmas.getOrDefault(stemm, 0) + 1);
            }
        }


        int docLen = annotation.get(CoreAnnotations.TokensAnnotation.class).size();
        for (String key: lemmas.keySet()) {
            if (maxLemmaTf < lemmas.get(key)) {
                maxLemmaTf = lemmas.get(key);
            }
            TreeSet<Integer> getValue = lemmaDict.getOrDefault(key, new TreeSet<>());
            getValue.add(docId);
            lemmaDict.put(key, getValue);
        }
        docLemmaPosting.put(docId, new DocumentPosting(docId, maxLemmaTf, docLen));

        for (String key: stemmas.keySet()) {
            if (maxStemmaTf < stemmas.get(key)) {
                maxStemmaTf = stemmas.get(key);
            }
            TreeSet<Integer> getValue = stemmaDict.getOrDefault(key, new TreeSet<>());
            getValue.add(docId);
            stemmaDict.put(key, getValue);
        }
        docStemmaPosting.put(docId, new DocumentPosting(docId, maxStemmaTf, docLen));

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
    //TODO: Remove numerical Data
    public static void getTextFromFile(String fileName) throws Exception {
        String xml = new String(Files.readAllBytes(Paths.get(fileName)));
        String input = xml;
        input = input.replaceAll("\n", " ");
        Matcher m = DOCID_PATTERN.matcher(input);
        int docId = -1;
        if (m.find() && !m.group(1).equals("")) {
           docId = Integer.parseInt(m.group(1).replaceAll(" ", ""));
        }
//        System.out.println("Document Id  : " + docId);
        xml = xml.replaceAll(Indexing.PATTERN, "");
        xml = xml.replaceAll(Indexing.NUMBER_PATTERN, "");
//        xml = xml.replaceAll("\\p{Punct}", "");
//        System.out.println(xml);
        getLemmaStemmaForDoc(xml, docId);
    }

    public static void processFilesFromFolder(File folder, String folderName) throws Exception {
        for (final File fileEntry : folder.listFiles()) {
            getTextFromFile(folderName + fileEntry.getName());
        }
    }

    public static void loadStopWords(String stopWordsFile) {
        try {
            stopWords.addAll( Files.readAllLines(Paths.get(stopWordsFile), StandardCharsets.UTF_8));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void loadPunctuations() {
        String[] punctuation = {".", ";", ",", "/", "'", "`" , "+", "-", "=", "/",
                "*", " ", "$", "-" , "(", ")", "?", ":", "", "--", "''"};
        punctuations.addAll(Arrays.asList(punctuation));
    }

    public static void main(String args[]) throws Exception{

        if (args.length < 2) {
            System.out.println("Incorrect Parameters ");
            System.exit(-1);
        }
        long start = System.currentTimeMillis();
        loadStopWords(args[1]);
        loadPunctuations();
        processFilesFromFolder(new File(args[0]), args[0] + '/');
        long end = System.currentTimeMillis();
        printMap(stemmaDict);
        printDictionary(docStemmaPosting);
        System.out.println("time taken :" + (end - start));
    }
}
