import DataModels.DocumentPosting;
import DataModels.TermPosting;
import IRUtilies.NLP;
import TestNLPPackage.TermDFTF;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
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

    //uncompressed
    private static Map<String, TermPosting> dictionaryLemma = new TreeMap<>();
    private static Map<String, TermPosting> dictionaryStemma = new TreeMap<>();

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

    public static void printMap(Map<String, TreeSet<Integer>> dict) {
        System.out.println("Dictionary Keys  : " + dict.size());
        for (String data: dict.keySet()) {
            System.out.println("--" + data + "--" + Arrays.toString(dict.get(data).toArray()));
        }
    }

    public static void printAll(Map<String, TreeSet<Integer>> dict,  //Integer posting list
                                Map<String, TermDFTF> doc,  //term data
                                Map<Integer, DocumentPosting> docPosting, //doc metadata
                                Map<String, TermPosting> dictionary //output
    )
    {
        System.out.println("_________________________________________________________________________");
        for (String key : dict.keySet()) {
            TermDFTF term = doc.get(key);
            TreeSet<Integer> postingList = dict.get(key);
            List<DocumentPosting> docList = new LinkedList<>();
            for (Integer docId : postingList) {
                docList.add(docPosting.get(docId));
            }
            TermPosting data = new TermPosting(key, term.getDf(), term.getTf(), docList);
//            System.out.println(data);
            dictionary.put(key, data);
        }
    }

    public static void updateDictionaries(int docLen, int docId, Map<String, Integer> dict,
                                   Map<String, TreeSet<Integer>> postingDict,
                                   Map<String, TermDFTF> termDFTFDict,
                                   Map<Integer, DocumentPosting> docPosting) {
        int maxTf = 0;
        for (String key: dict.keySet()) {
            if (maxTf < dict.get(key)) {
                maxTf = dict.get(key);
            }
            TreeSet<Integer> getValue = postingDict.getOrDefault(key, new TreeSet<>());
            getValue.add(docId);
            postingDict.put(key, getValue);

            TermDFTF dftf = termDFTFDict.getOrDefault(key, new TermDFTF(0,0));
            termDFTFDict.put(key, new TermDFTF(dftf.getDf() + 1, dftf.getTf() + dict.get(key)));
        }
        docPosting.put(docId, new DocumentPosting(docId, maxTf, docLen));
    }

    public static void getLemmaStemmaForDoc(String data, Integer docId) {

        //set properties to get tokens, split, pos tags and get its lemmas
        nlp.annotateData(data);
        Map<String, Integer> lemmas = new HashMap<>();
        Map<String, Integer> stemmas = new HashMap<>();
        for (CoreLabel token : nlp.getCoreLabels()) {
            String stemm = nlp.getStemma(token.originalText());
            if (!stopWords.contains(stemm) && !punctuations.contains(token.originalText())) {
                lemmas.put(token.lemma(), lemmas.getOrDefault(token.lemma(), 0) + 1);
                stemmas.put(stemm, stemmas.getOrDefault(stemm, 0) + 1);
            }
        }
        int docLen = nlp.getCoreLabels().size();
        updateDictionaries(docLen, docId, lemmas, lemmaDict,lemmaTFDict, docLemmaPosting);
        updateDictionaries(docLen, docId, stemmas, stemmaDict, stemmaTFDict, docStemmaPosting);
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

    public static void serializationOfDictionary(Map<String, TermPosting> dict) {
        // Serialization
        System.out.println("serializing obejct count : " + dict.size());
        try
        {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/SerializedUnCompressedLemma");
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(dict);
            out.close();
            file.close();
            System.out.println("Object has been serialized");

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
    }

    public static void deSerializationOfDictionary() {
        Map<String, TermPosting> dictionary = null;
        // Deserialization
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream("/home/sharayu/SEM3/Information Retrival/HOMEWORK/HW2/OutPut/SerializedUnCompressedLemma");
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            dictionary = (Map<String, TermPosting>)in.readObject();

            in.close();
            file.close();

            System.out.println("Object has been deserialized ");
        }
        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }

//        for (String key : dictionary.keySet()) {
//            System.out.println(dictionary.get(key));
//        }
        System.out.println("deserialized object count : " + dictionary.size());
    }

    public static void main(String args[]) throws Exception{

        if (args.length < 2) {
            System.out.println("Incorrect Parameters ");
            System.exit(-1);
        }

        loadStopWords(args[1]);
        loadPunctuations();
        long start = System.currentTimeMillis();
        processFilesFromFolder(new File(args[0]), args[0] + '/');
        printAll(stemmaDict, stemmaTFDict, docStemmaPosting, dictionaryStemma);
        printAll(lemmaDict, lemmaTFDict, docLemmaPosting, dictionaryLemma);
        long end = System.currentTimeMillis();
        System.out.println("time taken :" + (end - start));

        start = System.currentTimeMillis();
        //Serialize and write to file
        serializationOfDictionary(dictionaryStemma);
        end = System.currentTimeMillis();
        System.out.println("time taken to serialize :" + (end - start));

        start = System.currentTimeMillis();
        //deserialize and read from file and reconstruct
        System.out.println("Printing Result ");
        deSerializationOfDictionary();
        end = System.currentTimeMillis();
        System.out.println("time taken to deserialize :" + (end - start));

        //Compression
        //lemma blocked compression where k = 8 for dictionary and posting list gamma coding

        //Compression
        //stemma front coding for dictionary and posting list delta coding

    }
}
