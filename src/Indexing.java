import StanfordNLP.NLP;
import TestNLPPackage.TermDFTF;
import edu.stanford.nlp.ling.CoreLabel;

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
    private static Map<String, TermDFTF> lemmaTFDict = new TreeMap<>();
    private static Map<String, TermDFTF> stemmaTFDict = new TreeMap<>();
    private static Map<Integer, DocumentPosting> docLemmaPosting = new HashMap<>();
    private static Map<Integer, DocumentPosting> docStemmaPosting = new HashMap<>();
    private static NLP nlp = new NLP();

    public static void printTFDictionary(Map<String, TermDFTF> doc) {
        System.out.println("Doc Data : " );
        for (String docId : doc.keySet()) {
            System.out.println("Doc " + docId + " --> " + doc.get(docId));
        }
    }

    public static void printDictionary(Map<Integer, DocumentPosting> doc) {
        System.out.println("Doc Data : " );
        for (Integer docId : doc.keySet()) {
            System.out.println("Doc " + docId + " -->" + doc.get(docId).toString());
        }
    }

    public static void printAll(Map<String, TreeSet<Integer>> dict, Map<String, TermDFTF> doc) {
        for (String key : dict.keySet()) {
            System.out.println(" Term " + key  + doc.get(key) + " " + Arrays.toString(dict.get(key).toArray()));
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
        nlp.annotateData(data);
        Map<String, Integer> lemmas = new HashMap<>();
        Map<String, Integer> stemmas = new HashMap<>();
        int maxLemmaTf = 0;
        int maxStemmaTf = 0;

        for (CoreLabel token : nlp.getCoreLabels()) {
            String stemm = nlp.getStemma(token.originalText());
            if (!stopWords.contains(stemm) && !punctuations.contains(token.originalText())) {
                lemmas.put(token.lemma(), lemmas.getOrDefault(token.lemma(), 0) + 1);
                stemmas.put(stemm, stemmas.getOrDefault(stemm, 0) + 1);
            }
        }


        int docLen = nlp.getCoreLabels().size();
        for (String key: lemmas.keySet()) {
            if (maxLemmaTf < lemmas.get(key)) {
                maxLemmaTf = lemmas.get(key);
            }
            TreeSet<Integer> getValue = lemmaDict.getOrDefault(key, new TreeSet<>());
            getValue.add(docId);
            lemmaDict.put(key, getValue);

            TermDFTF dftf = lemmaTFDict.getOrDefault(key, new TermDFTF(0,0));
            lemmaTFDict.put(key, new TermDFTF(dftf.getDf() + 1, dftf.getTf() + lemmas.get(key)));
        }
        docLemmaPosting.put(docId, new DocumentPosting(docId, maxLemmaTf, docLen));

        for (String key: stemmas.keySet()) {
            if (maxStemmaTf < stemmas.get(key)) {
                maxStemmaTf = stemmas.get(key);
            }
            TreeSet<Integer> getValue = stemmaDict.getOrDefault(key, new TreeSet<>());
            getValue.add(docId);
            stemmaDict.put(key, getValue);

            TermDFTF dftf = stemmaTFDict.getOrDefault(key, new TermDFTF(0,0));
            stemmaTFDict.put(key, new TermDFTF(dftf.getDf() + 1, dftf.getTf() + stemmas.get(key)));
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

        loadStopWords(args[1]);
        loadPunctuations();
        long start = System.currentTimeMillis();
//        Indexing indexing = new Indexing(args[1]);
        processFilesFromFolder(new File(args[0]), args[0] + '/');
        long end = System.currentTimeMillis();
        printAll(stemmaDict, stemmaTFDict);
        System.out.println("time taken :" + (end - start));
    }
}
