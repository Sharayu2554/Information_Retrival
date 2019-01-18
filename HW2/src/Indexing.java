import IRUtilies.Codes;
import Constants.FileNames;
import DataModels.*;
import IRUtilies.ByteOperations;
import IRUtilies.LCP;
import IRUtilies.NLP;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexing {

    //regex to file all the tags <__TAG__> in file
    private static final String PATTERN = "<.*>";

    //regex to file all the numbers in files
    private static final String NUMBER_PATTERN = "\\d+";

    //regex to get docId from file
    private static final Pattern DOCID_PATTERN = Pattern.compile("<DOCNO>(.*)</DOCNO>");

    private static final HashSet<String> stopWords = new HashSet<>();
    private static final HashSet<String> punctuations = new HashSet<>();

    private static Map<String, TreeSet<Integer>> lemmaDict = new TreeMap<>();
    private static Map<String, TreeSet<Integer>> stemmaDict = new TreeMap<>();
    private static Map<String, TermDFTF> lemmaTFDict = new TreeMap<>();
    private static Map<String, TermDFTF> stemmaTFDict = new TreeMap<>();
    private static Map<Integer, DocumentPosting> docLemmaPosting = new TreeMap<>();
    private static Map<Integer, DocumentPosting> docStemmaPosting = new TreeMap<>();
    private static NLP nlp = new NLP();

    public static void printTFDictionary(Map<String, TermDFTF> doc) {
        System.out.println("Doc Data : " );
        for (String docId : doc.keySet()) {
            System.out.println("Doc " + docId + " --> " + doc.get(docId));
        }
    }

    public static void printDocDictionary(Map<Integer, DocumentPosting> doc) {
        System.out.println("Doc Data : " );
        for (Integer docId : doc.keySet()) {
            System.out.println("Doc " + docId + " -->" + doc.get(docId).toString());
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
     * Apply regex to remove new lines, get docId, replace tags with empty, remove all numbers from text
     * @param fileName (path to file name as paramater)
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
        getLemmaStemmaForDoc(xml, docId);
    }

    /**
     * for every file in data directory, it calls getTextFromFile function with path to file name as paramater
     * @param folder (file pointer to data directory)
     * @param folderName (path to directory in string format)
     * @throws Exception
     */
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

    public static void writeLemmaUncompressed(String pathToDir) throws IOException {
        //work with dictionaryLemma
        //write to file lemma uncompressed

        //write docInfo (docId, maxtf, docLen)(4 bytes, 4 bytes, 4 bytes)(1400 * 12) = (16.8k)
        //docLemmaPosting (docId, maxtf, docLen)
        FileOutputStream fos = new FileOutputStream(pathToDir + FileNames.INDEX_UNCOMPRESS_VERSION1_DOCINFO);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        Map<Integer, Integer> docPointers = new HashMap<>();
        int pos = 0;
        int docCount = 0;
        for (Integer docId : docLemmaPosting.keySet()) {
            DocumentPosting data = docLemmaPosting.get(docId);
            byte[] bDocId = ByteOperations.IntToByte(docId);
            byte[] bMaxTf = ByteOperations.IntToByte(data.getMaxTf());
            byte[] bDocLen = ByteOperations.IntToByte(data.getDocLen());

            bos.write(bDocId);
            bos.write(bMaxTf);
            bos.write(bDocLen);

            docPointers.put(docId, pos);
            pos = pos + bDocId.length + bMaxTf.length + bDocLen.length;
            docCount += 1;
        }
        System.out.println("Document Count : " + docCount);
        System.out.println("Doc pos : " + pos);
        bos.close();
        fos.close();

        //write posting_ptr (4 bytes per document)
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_UNCOMPRESS_VERSION1_POSTING_PTR);
        bos = new BufferedOutputStream(fos);
        pos = 0;
        Map<String, Integer> postingPtr = new HashMap<>();
        for (String term : lemmaDict.keySet()) {
            Set<Integer> postings = lemmaDict.get(term);
            postingPtr.put(term, pos);
            for (Integer docId : postings) {
                byte[] bDocIdPosition = ByteOperations.IntToByte(docPointers.get(docId));
                bos.write(bDocIdPosition);
                pos = pos + bDocIdPosition.length;
            }
        }
        System.out.println("Posting pointer Pos : " + pos);
        bos.close();
        fos.close();

        //write index (34 bytes for term, 4 bytes for df, 4 bytes for tf, 4 bytes for pointer)( 46 bytes each term)(46 * 8525 )(392K)
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_UNCOMPRESS_VERSION1_INDEX);
        bos = new BufferedOutputStream(fos);
        pos = 0;
        int termCount = 0;
        for (String term : lemmaTFDict.keySet()) {
            TermDFTF data = lemmaTFDict.get(term);
            byte[] bTerm = ByteOperations.StringToByte(term);
            byte[] bDf = ByteOperations.IntToByte(data.getDf());
            byte[] bTf = ByteOperations.IntToByte(data.getTf());
            byte[] bPosting = ByteOperations.IntToByte(postingPtr.get(term));

            bos.write(bTerm);
            bos.write(bDf);
            bos.write(bTf);
            bos.write(bPosting);
            pos = pos + bTerm.length + bDf.length + bTf.length + bPosting.length;
            termCount += 1;
        }
        System.out.println("Index pos : " + pos);
        System.out.println("Term Count : " + termCount);
        bos.close();
        fos.close();


    }

    public static void writeStemmaUncompressed(String pathToDir) throws IOException {
        //work with dictionaryStemma
        //write to file stemma uncompressed

        //write docInfo (docId, maxtf, docLen)(4 bytes, 4 bytes, 4 bytes)(1400 * 12) = (16.8k)
        //docLemmaPosting (docId, maxtf, docLen)
        FileOutputStream fos = new FileOutputStream(pathToDir + FileNames.INDEX_UNCOMPRESS_VERSION2_DOCINFO);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        Map<Integer, Integer> docPointers = new HashMap<>();
        int pos = 0;
        int docCount = 0;
        for (Integer docId : docStemmaPosting.keySet()) {
            DocumentPosting data = docStemmaPosting.get(docId);
            byte[] bDocId = ByteOperations.IntToByte(docId);
            byte[] bMaxTf = ByteOperations.IntToByte(data.getMaxTf());
            byte[] bDocLen = ByteOperations.IntToByte(data.getDocLen());

            bos.write(bDocId);
            bos.write(bMaxTf);
            bos.write(bDocLen);

            docPointers.put(docId, pos);
            pos = pos + bDocId.length + bMaxTf.length + bDocLen.length;
            docCount += 1;
        }
        System.out.println("Document Count : " + docCount);
        System.out.println("Doc pos : " + pos);
        bos.close();
        fos.close();

        //write posting_ptr (4 bytes per document)
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_UNCOMPRESS_VERSION2_POSTING_PTR);
        bos = new BufferedOutputStream(fos);
        pos = 0;
        Map<String, Integer> postingPtr = new HashMap<>();
        for (String term : stemmaDict.keySet()) {
            Set<Integer> postings = stemmaDict.get(term);
            postingPtr.put(term, pos);
            for (Integer docId : postings) {
                byte[] bDocIdPosition = ByteOperations.IntToByte(docPointers.get(docId));
                bos.write(bDocIdPosition);
                pos = pos + bDocIdPosition.length;
            }
        }
        System.out.println("Posting pointer Pos : " + pos);
        bos.close();
        fos.close();

        //write index (34 bytes for term, 4 bytes for df, 4 bytes for tf, 4 bytes for pointer)( 46 bytes each term)(46 * 8525 )(392K)
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_UNCOMPRESS_VERSION2_INDEX);
        bos = new BufferedOutputStream(fos);
        pos = 0;
        int termCount = 0;
        for (String term : stemmaTFDict.keySet()) {
            TermDFTF data = stemmaTFDict.get(term);
            byte[] bTerm = ByteOperations.StringToByte(term);
            byte[] bDf = ByteOperations.IntToByte(data.getDf());
            byte[] bTf = ByteOperations.IntToByte(data.getTf());
            byte[] bPosting = ByteOperations.IntToByte(postingPtr.get(term));

            bos.write(bTerm);
            bos.write(bDf);
            bos.write(bTf);
            bos.write(bPosting);
            pos = pos + bTerm.length + bDf.length + bTf.length + bPosting.length;
            termCount += 1;
        }
        System.out.println("Index pos : " + pos);
        System.out.println("Term Count : " + termCount);
        bos.close();
        fos.close();

    }

    public static void writeLemmaCompressed(String pathToDir) throws IOException {

        //write docInfo
        //write docInfo (docId, maxtf, docLen)(4 bytes, 4 bytes, 4 bytes)(1400 * 12) = (16.8k)
        //docLemmaPosting (docId, maxtf, docLen)
        FileOutputStream fos = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION1_DOCINFO);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        Map<Integer, Integer> docPointers = new HashMap<>();
        int pos = 0;
        int docCount = 0;
        for (Integer docId : docLemmaPosting.keySet()) {
            DocumentPosting data = docLemmaPosting.get(docId);
            byte[] bDocId = ByteOperations.IntToByte(docId);
            byte[] bMaxTf = ByteOperations.IntToByte(data.getMaxTf());
            byte[] bDocLen = ByteOperations.IntToByte(data.getDocLen());

            bos.write(bDocId);
            bos.write(bMaxTf);
            bos.write(bDocLen);

            docPointers.put(docId, pos);
            pos = pos + bDocId.length + bMaxTf.length + bDocLen.length;
            docCount += 1;
        }
        System.out.println("Document Count : " + docCount);
        System.out.println("Doc pos : " + pos);
        bos.close();
        fos.close();

        //posting ptr with gaps and gamma codes
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION1_POSTING_PTR);
        bos = new BufferedOutputStream(fos);
        pos = 0;
        int posCount = 0;
        Map<String, Integer> postingPtr = new HashMap<>();
        System.out.println("Total Number of Terms : " + lemmaDict.size());
        for (String term : lemmaDict.keySet()) {
            postingPtr.put(term, pos);
            Set<Integer> postings = lemmaDict.get(term);
            boolean flag = true;
            int prev = 0;
            for (Integer docId : postings) {
                if (flag) {
                    prev = docId;

                    byte[] bDocId = Codes.gammaCode(docId);
                    bos.write(bDocId);
                    pos = pos + bDocId.length;
                    flag = false;
                    continue;
                }
                byte[] bDocId = Codes.gammaCode(docId - prev);
                bos.write(bDocId);
                pos = pos + bDocId.length;
                prev = docId;
            }
            posCount += 1;
        }
        System.out.println("Posting pointer Pos : " + pos);
        System.out.println("Posting pointer Pos Count : " + posCount);
        bos.close();
        fos.close();

        //create terms file
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION1_TERM);
        bos = new BufferedOutputStream(fos);

        FileOutputStream fosIndex = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION1_INDEX);
        BufferedOutputStream bosIndex = new BufferedOutputStream(fosIndex);

        int k = 0;
        pos = 0;
        int termLengthCount = 0;
        int posIndex = 0;
        byte[] termPointer;
        for (String term : lemmaDict.keySet()) {
            termLengthCount += term.length();
            if (k == 0) {
                //put pos of term in bosIndex
                termPointer = ByteOperations.IntToByte(pos);
                bosIndex.write(termPointer);
                posIndex += termPointer.length;
            }

            byte[] len = ByteOperations.LenToByte(term.length());
            byte[] termb = ByteOperations.StringFlexToByte(term);
//            System.out.println(len.length + " bytes : " + term.length());
            bos.write(len);
            bos.write(termb);
            pos += len.length + termb.length;

            TermDFTF termData = lemmaTFDict.get(term);
            byte[] dfb = ByteOperations.IntToByte(termData.getDf());
            byte[] tfb = ByteOperations.IntToByte(termData.getTf());
            byte[] postingPtrb = ByteOperations.IntToByte(postingPtr.get(term));
            bosIndex.write(dfb);
            bosIndex.write(tfb);
            bosIndex.write(postingPtrb);
            posIndex += dfb.length + tfb.length + postingPtrb.length;
            k += 1;
            if (k > 7) {
                 k = 0;
            }
        }
        System.out.println("Term length count = " + (termLengthCount + 70));
        //create index
        bos.close();
        bosIndex.close();
        fos.close();
        fosIndex.close();

        System.out.println("TERM pos count : " + pos);
        System.out.println("Index pos count : " + posIndex);
    }

    public static void writeStemmaCompressed(String pathToDir) throws IOException {

        //write docInfo
        //write docInfo (docId, maxtf, docLen)(4 bytes, 4 bytes, 4 bytes)(1400 * 12) = (16.8k)
        //docLemmaPosting (docId, maxtf, docLen)
        FileOutputStream fos = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION2_DOCINFO);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        Map<Integer, Integer> docPointers = new HashMap<>();
        int pos = 0;
        int docCount = 0;
        for (Integer docId : docStemmaPosting.keySet()) {
            DocumentPosting data = docStemmaPosting.get(docId);
            byte[] bDocId = ByteOperations.IntToByte(docId);
            byte[] bMaxTf = ByteOperations.IntToByte(data.getMaxTf());
            byte[] bDocLen = ByteOperations.IntToByte(data.getDocLen());

            bos.write(bDocId);
            bos.write(bMaxTf);
            bos.write(bDocLen);

            docPointers.put(docId, pos);
            pos = pos + bDocId.length + bMaxTf.length + bDocLen.length;
            docCount += 1;
        }
        System.out.println("Document Count : " + docCount);
        System.out.println("Doc pos : " + pos);
        bos.close();
        fos.close();

        //posting ptr with gaps and gamma codes
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION2_POSTING_PTR);
        bos = new BufferedOutputStream(fos);
        pos = 0;
        int posCount = 0;
        Map<String, Integer> postingPtr = new HashMap<>();
        System.out.println("Total Number of Terms : " + stemmaDict.size());
        for (String term : stemmaDict.keySet()) {
            postingPtr.put(term, pos);
            Set<Integer> postings = stemmaDict.get(term);
            boolean flag = true;
            int prev = 0;
            for (Integer docId : postings) {
                if (flag) {
                    prev = docId;

                    byte[] bDocId = Codes.deltaCode(docId);
                    bos.write(bDocId);
                    pos = pos + bDocId.length;
                    flag = false;
                    continue;
                }
                byte[] bDocId = Codes.deltaCode(docId - prev);
                bos.write(bDocId);
                pos = pos + bDocId.length;
                prev = docId;
            }
            posCount += 1;
        }
        System.out.println("Posting pointer Pos : " + pos);
        System.out.println("Posting pointer Pos Count : " + posCount);
        bos.close();
        fos.close();

        //create terms file
        fos = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION2_TERM);
        bos = new BufferedOutputStream(fos);

        FileOutputStream fosIndex = new FileOutputStream(pathToDir + FileNames.INDEX_COMPRESS_VERSION2_INDEX);
        BufferedOutputStream bosIndex = new BufferedOutputStream(fosIndex);

        int k = 0;
        pos = 0;
        int termLengthCount = 0;
        int posIndex = 0;
        byte[] termPointer;
        String[] termArray = new String[8];
        for (String term : stemmaDict.keySet()) {
            termLengthCount += term.length();
            if (k == 0) {
                //put pos of term in bosIndex
                termPointer = ByteOperations.IntToByte(pos);
                bosIndex.write(termPointer);
                posIndex += termPointer.length;
            }

            termArray[k] = term;
            TermDFTF termData = stemmaTFDict.get(term);
            byte[] dfb = ByteOperations.IntToByte(termData.getDf());
            byte[] tfb = ByteOperations.IntToByte(termData.getTf());
            byte[] postingPtrb = ByteOperations.IntToByte(postingPtr.get(term));
            bosIndex.write(dfb);
            bosIndex.write(tfb);
            bosIndex.write(postingPtrb);
            posIndex += dfb.length + tfb.length + postingPtrb.length;
            k += 1;
            if (k > 7) {
                pos += generateFrontCodingAndWrite(termArray, bos);
                k = 0;
            }
        }
        System.out.println("Term length count = " + (termLengthCount + 70));
        bos.close();
        bosIndex.close();
        fos.close();
        fosIndex.close();

        System.out.println("TERM pos count : " + pos);
        System.out.println("Index pos count : " + posIndex);

    }

    public static int generateFrontCodingAndWrite(String[] input, BufferedOutputStream bos) throws IOException {
        String sub = LCP.longestCommonPrefix(input);
        List<Byte> list = new LinkedList<Byte>();

        byte[] blen = ByteOperations.LenToByte(input[0].length());
        byte[] bsub = ByteOperations.StringFlexToByte(sub + "*");
        bos.write(blen);
        bos.write(bsub);
//        String res = input[0].length() + sub + "*";
        int pos = blen.length + bsub.length;
        boolean flag = true;
        for (String in : input) {
            String inSub = in.substring(sub.length(), in.length());
            byte[] binSub = ByteOperations.StringFlexToByte(inSub);
            byte[] binSubToRes;
            if (flag) {
                binSubToRes = ByteOperations.StringFlexToByte(inSub);
                bos.write(binSubToRes);
                pos += binSubToRes.length;
//                res += inSub;
                flag = false;
                continue;
            }
            byte[] binSubLen = ByteOperations.LenToByte(inSub.length());
            binSubToRes = ByteOperations.StringFlexToByte("#" + inSub);
            bos.write(binSubLen);
            bos.write(binSubToRes);
            pos += binSubLen.length + binSubToRes.length;
//            res += inSub.length() + "#" + inSub;
        }
        return pos;
    }

    public static void main(String args[]) throws Exception{


        if (args.length < 3) {
            System.out.println("Incorrect Parameters ");
            System.exit(-1);
        }

        String pathToOutDir = args[2];
        loadStopWords(args[1]);
        loadPunctuations();

        long start = System.currentTimeMillis();
        processFilesFromFolder(new File(args[0]), args[0] + '/');
        long end = System.currentTimeMillis();
        System.out.println("\nTime Taken to Process data into dictionary (ms):" + (end - start) + "ms");

        //write to file lemma uncompressed
        System.out.println("\nLemma ");
        start = System.currentTimeMillis();
        writeLemmaUncompressed(pathToOutDir);
        end = System.currentTimeMillis();
        System.out.println("time taken to write Index1(Lemma) Uncompressed File (ms):" + (end - start) + "ms");

        //write to file stemma uncompressed
        System.out.println("\nStemma ");
        start = System.currentTimeMillis();
        writeStemmaUncompressed(pathToOutDir);
        end = System.currentTimeMillis();
        System.out.println("time taken to write Index2(Stemma) Uncompressed File (ms):" + (end - start) + "ms");

        //Compression
        //lemma blocked compression where k = 8 for dictionary and posting list gamma coding
        System.out.println("\nLemma Block Compressed ");
        start = System.currentTimeMillis();
        writeLemmaCompressed(pathToOutDir);
        end = System.currentTimeMillis();
        System.out.println("time taken to write Index1(Lemma) Compressed File (ms):" + (end - start) + "ms");

        //Compression
        //stemma front coding for dictionary and posting list delta coding
        System.out.println("\nStemma Front Coding and Compressed");
        start = System.currentTimeMillis();
        writeStemmaCompressed(pathToOutDir);
        end = System.currentTimeMillis();
        System.out.println("time taken to write Index2(Stemma) Compressed File (ms):" + (end - start) + "ms");

        System.out.println("\nExtra Point Questions\n");
        //df, tf, posting list
        HashSet<String> output = new HashSet<>();
        output.add("reynold");
        output.add("nasa");
        output.add("prandtl");
        output.add("flow");
        output.add("pressure");
        output.add("boundary");
        output.add("shock");

        for (String term : output) {
            if (lemmaTFDict.containsKey(term)) {
                System.out.println(term + " " + lemmaTFDict.get(term) + " posting list " + Arrays.toString(lemmaDict.get(term).toArray()));
            }
        }


        //if NASA, df, tf, posting list
        //for first three entries in posting list
        //maxtf, doclen

        if (lemmaTFDict.containsKey("nasa")) {
            System.out.println("nasa : " + lemmaTFDict.get("nasa") + " posting list " + Arrays.toString(lemmaDict.get("nasa").toArray()));
            int  i = 0;
            for (Integer docId: lemmaDict.get("nasa")) {
                if (i > 3) {
                    break;
                }
                System.out.println("docId " + docId + " : " + docLemmaPosting.get(docId));
                i++;
            }
        }

        //term with largest df index1
        //term with lowest df index1
        int maxdf = 0;
        String maxdfterm = "";
        int mindf = Integer.MAX_VALUE;
        String mindfterm = "";
        for (String term : lemmaTFDict.keySet()) {
            TermDFTF data = lemmaTFDict.get(term);
            if (maxdf < data.getDf()) {
                maxdf = data.getDf();
                maxdfterm = term;
            }
            if (mindf > data.getDf()) {
                mindf = data.getDf();
                mindfterm = term;
            }
        }
        System.out.println("\nTerm with largest df in index1 : " + maxdfterm + " max df is " + maxdf);
        System.out.println("Term with lowest df in index1 : " + mindfterm + " min df is " + mindf);

        //term with largest df index2
        //term with lowest df index2
        maxdf = 0;
        maxdfterm = "";
        mindf = Integer.MAX_VALUE;
        mindfterm = "";
        for (String term : stemmaTFDict.keySet()) {
            TermDFTF data = stemmaTFDict.get(term);
            if (maxdf < data.getDf()) {
                maxdf = data.getDf();
                maxdfterm = term;
            }
            if (mindf > data.getDf()) {
                mindf = data.getDf();
                mindfterm = term;
            }
        }
        System.out.println("\nTerm with largest df in index2 : " + maxdfterm + " max df is " + maxdf);
        System.out.println("Term with lowest df in index2 : " + mindfterm + " min df is " + mindf);

        //doc with largest maxtf
        //doc with largest doclen
        int maxtf = 0;
        int maxtfdoc = 0;
        int maxdoclendoc = 0;
        int maxdocLen = 0;
        for (Integer docId : docLemmaPosting.keySet()) {
            DocumentPosting data = docLemmaPosting.get(docId);
            if (maxtf < data.getMaxTf()) {
                maxtf = data.getMaxTf();
                maxtfdoc = docId;
            }
            if (maxdocLen < data.getDocLen()) {
                maxdocLen = data.getDocLen();
                maxdoclendoc = docId;
            }
        }
        System.out.println("\nDoc with largest maxtf in collection : " + maxtfdoc + " docId is " + maxtfdoc);
        System.out.println("Doc with largest max docLen in collection : " + maxdocLen + " docId is " + maxdoclendoc);

    }
}
