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
    private static long collectionSize = 0;
    private static long sumOfDocLens = 0;
    private static Map<String, Integer> globalLemmas = new HashMap<>();
    private static Map<String, Integer> globalStemmas = new HashMap<>();

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
            if (!stopWords.contains(stemm) && !punctuations.contains(token.originalText())) {

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
        System.out.println("Document " + docId);
        for (String token: stemmas.keySet()) {
            System.out.println(" token: " + token + " : " + stemmas.get(token) );
        }
        updateDictionaries(docLen, docId, lemmas, globalLemmas);
        updateDictionaries(docLen, docId, stemmas, globalStemmas);
    }

    public static void main(String args[]) {
        String[] docs = new String[10];
        loadStopWords(args[0]);
        loadPunctuations();
        int n = 2;
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
        System.out.println();
    }
}
