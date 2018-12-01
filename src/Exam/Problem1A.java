package Exam;

import edu.stanford.nlp.ling.CoreLabel;
import src.DataModels.DocumentPosting;
import src.DataModels.PostingDoc;
import src.DataModels.TermDFTF;
import src.IRUtilies.NLP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Problem1A {
    private static NLP nlp = new NLP();
    private static final HashSet<String> stopWords = new HashSet<>();
    private static final HashSet<String> punctuations = new HashSet<>();
    private static Map<Integer, TreeMap<String, Integer>> docLemmaData = new HashMap<>();
    private static Map<Integer, TreeMap<String, Integer>> docStemmaData = new HashMap<>();
    private static int collectionSize = 0;
    private static long sumOfDocLens = 0;
    private static Map<String, Integer> globalLemmas = new TreeMap<>();
    private static Map<String, Integer> globalStemmas = new TreeMap<>();

    private static Map<Integer, TreeMap<String, Integer>> vectorLemmaData = new HashMap<>();
    private static Map<Integer, TreeMap<String, Integer>> vectorStemmaData = new HashMap<>();

    private static int[][] vectorArrayLemmaData;
    private static int[][] vectorArrayStemmaData;

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

    public static void updateDictionaries(int docLen, int docId, Map<String, Integer> dict,
                                          Map<String, Integer> gloabalDict) {
        int maxTf = 0;
        for (String key: dict.keySet()) {
            gloabalDict.put(key, gloabalDict.getOrDefault(key, 0) + 1);
        }
    }

    /**
     * for each file
     * tokenize the file (Stanford NLP)
     * apply pos tagger to the array (Stanford NLP)
     * lemmatize those words (Stanford NLP)
     * remove punctuations
     * remove stop words
     * create dictionary
     * add to posting list (Tree Set takes care of unique doc Ids and sorted order)
     * sort the dict (TreeMap takes care of that)
     * @param data (text in  file)
     * @param docId
     */
    public static void getStemmaForDoc(String data, Integer docId) {

        //set properties to get tokens, split, pos tags and get its lemmas
        data = data.replaceAll("\n", " ");
        data = data.replaceAll("\\d","");
        nlp.annotateData(data);
        Map<String, Integer> lemmas = new TreeMap<>();
        Map<String, Integer> stemmas = new TreeMap<>();
        int count = 0;
        for (CoreLabel token : nlp.getCoreLabels()) {
            String stemm = nlp.getStemma(token.originalText());
            if (!stemm.isEmpty() && !stopWords.contains(stemm) && !punctuations.contains(token.originalText())) {

                lemmas.put(token.lemma(), lemmas.getOrDefault(token.lemma(), 0) + 1);
                stemmas.put(stemm, stemmas.getOrDefault(stemm, 0) + 1);
                count+= 1;

                TreeMap<String, Integer> docData = docLemmaData.getOrDefault(docId, new TreeMap<>());
                Integer tokenCount = docData.getOrDefault(token.lemma(), 0);
                docData.put(token.lemma(), tokenCount + 1);
                docLemmaData.put(docId, docData);

                docData = docStemmaData.getOrDefault(docId, new TreeMap<>());
                tokenCount = docData.getOrDefault(stemm, 0);
                docData.put(stemm, tokenCount + 1);
                docStemmaData.put(docId, docData);
            }
        }
        int docLen = count;
        collectionSize += 1;
        sumOfDocLens += docLen;
//        System.out.println("Document " + docId);
//        for (String token: stemmas.keySet()) {
//            System.out.println(" token: " + token + " : " + stemmas.get(token) );
//        }
        updateDictionaries(docLen, docId, lemmas, globalLemmas);
        updateDictionaries(docLen, docId, stemmas, globalStemmas);
    }

    public static void getDocumentVectors() {

        vectorArrayStemmaData = new int[collectionSize][globalStemmas.size()];
        vectorArrayLemmaData = new int[collectionSize][globalLemmas.size()];
        for (int docId = 0; docId < collectionSize; docId++) {

            System.out.println("vector model for document : " + docId);
            int index = 0;
            TreeMap<String, Integer> docData = docStemmaData.get(docId);
            TreeMap<String, Integer> vectorDocData = new TreeMap<String, Integer>();
            for (String token : globalStemmas.keySet()) {
                vectorDocData.put(token, docData.getOrDefault(token, 0));
                vectorArrayStemmaData[docId][index++] = docData.getOrDefault(token, 0);
            }
            vectorStemmaData.put(docId, vectorDocData);


            index = 0;
            docData = docLemmaData.get(docId);
            vectorDocData = new TreeMap<String, Integer>();
            for (String token : globalLemmas.keySet()) {
                vectorDocData.put(token, docData.getOrDefault(token, 0));
                vectorArrayLemmaData[docId][index++] = docData.getOrDefault(token, 0);
            }
            vectorLemmaData.put(docId, vectorDocData);
        }
    }



    /**
     * kmeans clustering
     * @param k
     */
    public static void kmeans(int k) {
        int[] seeds = new int[k];
        Set<Integer> seedSet = new HashSet<>();
        Random random  = new Random();
        int num =  random.nextInt(collectionSize -1);
        for (int i =0; i< k; i++) {
            while (seedSet.contains(num)) {
                num = random.nextInt(collectionSize -1);
            }
            seeds[i] = num;
            seedSet.add(num);
            System.out.print(seeds[i]  + ", ");
        }

    }

    public static void main(String args[]) {
        String[] docs = new String[10];
        loadStopWords(args[0]);
        loadPunctuations();
        int n = 10;
        docs[0] = "Sipping Chianti in rural Tuscany. Eating pizza on a Rome backstreet. Or exploring\n" +
                "ancient history at Pompeii.\n";
        docs[1] = "One in eight children and young people between the ages of 5 and 19 in England has a\n" +
                "mental disorder, according to a new report.";
        docs[2] = "The romantic boulevards and cobbled streets of Paris. The sparkling waters of the\n" +
                "Cote d'Azur. The slow-paced villages of Provence. Each is enough to make travelers\n" +
                "swoon.";
        docs[3] = "A troubling gap in life expectancy among the rich and poor has emerged in the United\n" +
                "Kingdom, and researchers say it also has been seen in the United States.\n";
        docs[4] = "The National Weather Service defines a hurricane as a \"tropical cyclone with maximum\n" +
                "sustained winds of 74 mph (64 knots) or higher.\n";
        docs[5] = "Celery also provides a healthy dose of fiber, as well as vitamins C and K and\n" +
                "potassium, and it is a very low-calorie snack.";
        docs[6] = "Tinseltown. La La Land. Los Angeles is easily reduced to cliché. But it doesn't take\n" +
                "much to discover there's so much more than the glitz and glamor for which it's\n" +
                "renowned. ";
        docs[7] = "The Sunshine State suffered the brunt of Hurricane Michael's punishing winds, which\n" +
                "decimated beach towns and left little more than debris in their wake.\n";
        docs[8] = "Lake effect snow is expected to pound areas near the eastern Great Lakes on Tuesday\n" +
                "into Wednesday -- especially just south and east of Buffalo, New York, and far\n" +
                "northwestern Pennsylvania.\n";
        docs[9] = "After two and a half weeks of historic destruction, the Camp Fire in Northern\n" +
                "California is 100% contained, but the search for remains threatens to push the death\n" +
                "toll over 88, where it stood late Monday.";

        for (int i = 0; i < n; i++) {
            getStemmaForDoc(docs[i], i);
        }
        getDocumentVectors();

        System.out.println("\nGlobal Stemma Values : " + globalStemmas.size());
        for (String token: globalStemmas.keySet()) {
            System.out.println(" token: " + token + " : " + globalStemmas.get(token) );
        }
        System.out.println();

//        TreeMap<String, Integer> vector = vectorStemmaData.get(0);
//        System.out.println("\nVector for doc1: " + vector.size());
//        int i = 0;
//        for (String token: vector.keySet()) {
//            System.out.println(" token: " + token + " : " + vector.get(token) + " : " + vectorArrayStemmaData[0][i++] );
//        }
        kmeans(3);
    }
}
