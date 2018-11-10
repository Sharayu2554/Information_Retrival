package src;

import edu.stanford.nlp.util.Index;
import src.DataModels.DocumentPosting;
import src.DataModels.PostingDoc;
import src.DataModels.TermDFTF;
import src.IRUtilies.NLP;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Retrival {

    /**
     * W1 = (0.4 + 0.6 * log (tf + 0.5) / log (maxtf + 1.0))
     * * (log (collectionsize / df)/ log (collectionsize)
     */
    public static double getWt1(int tf, int maxTf, int df, long N) {
        if (df == 0 || N == 0) {
            return 0;
        }
        return (0.4 + (0.6 * (Math.log(tf + 0.5)/(Math.log(maxTf + 1.0))))) * ((Math.log(N/df)/Math.log(N)));
    }


    /**
     * W2 = (0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 *
     * (doclen / avgdoclen))) * log (collectionsize / df)/
     * log (collectionsize))
     */
    public static double getWt2(int tf, int doclen, double avgdoclen, int df, long N) {
        if (df == 0 || N == 0 || avgdoclen == 0) {
            return 0;
        }
        return ((0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 * (doclen / avgdoclen)))) *  ((Math.log(N/df)/Math.log(N))));
    }

    public static double getWt(int type, int tf, int maxTf, int docLen, int df, long N) {
        switch(type) {
            case 1:
                return getWt1(tf, maxTf, df, N);
            case 2:
                return getWt2(tf, docLen, Indexing.getSumOfDocLens()/N, df, N);
            default:
                return 0.0;
        }
    }

    /**
     * parse it by determining tokens
     * discard stop words
     * generate lemmas for the content words
     * compute weights of the query vector
     * @param query
     */
    public static void processQueryToGetVectorRepresentation(String query, Map<Integer, Double> normalizedWt, int type, BufferedWriter writer) throws  IOException {

        NLP nlp  = Indexing.getNlp();
        nlp.annotateData(query);

        Map<String, TermDFTF> lemmaTfDict = Indexing.getLemmaTFDict();
        HashSet<String> stopWords = Indexing.getStopWords();
        HashSet<String> punctuations = Indexing.getPunctuations();

        Map<String, Integer> lemmas = new HashMap<>();
        int maxtf = 0;
        for (CoreLabel token : nlp.getCoreLabels()) {
            String stemm = nlp.getStemma(token.originalText());
            if (!stopWords.contains(stemm) && !punctuations.contains(token.originalText())) {
                lemmas.put(token.lemma(), lemmas.getOrDefault(token.lemma(), 0) + 1);
                if (maxtf < lemmas.get(token.lemma()))  {
                    maxtf = lemmas.get(token.lemma());
                }
            }
        }
        double sumOfSquaresQ = 0;

        //term in query and its wt (not normalized)
        Map<String, Double> termQueryQt = new TreeMap<>();

        //final scores of query for given document
        Map<Integer, Double> finalScores = new HashMap<>();

        for (String token: lemmas.keySet()) {
            int tf = lemmas.get(token);
            int df = lemmaTfDict.getOrDefault(token, new TermDFTF(0, 0)).getDf();
            //int type, int tf, int maxTf, int docLen, int df, int N
            double wtq =  getWt(type, tf, maxtf, lemmas.size(),  df, Indexing.getCollectionSize());
            termQueryQt.put(token, wtq);
            sumOfSquaresQ += wtq * wtq;

       }

        //normalize wt of each token by diving it by its sumOfSquaresQ
        //after this loop, termQueryQt will contain normalized wt
        double sqrtQ = Math.sqrt(sumOfSquaresQ);
        for (String token: lemmas.keySet()) {
            double normalizeQueryToken = termQueryQt.get(token)/sqrtQ;
            termQueryQt.put(token, normalizeQueryToken);

            TreeSet<PostingDoc> postingList = Indexing.getLemmaDict().getOrDefault(token, new TreeSet<>());
            for (PostingDoc doc: postingList) {
                DocumentPosting data = Indexing.getDocLemmaPosting().get(doc.getDocId());

                //getting normalized value for particular and specific token
                double normalizeDocToken = doc.getWt() / (normalizedWt.get(doc.getDocId()));

                //caclulate score for token in query and token in specific doc score and store in doc scoring
                finalScores.put(doc.getDocId(), finalScores.getOrDefault(doc.getDocId(), 0.0) + (normalizeDocToken * normalizeQueryToken));
            }
        }

        //sort map of docId keys with values in descending order to get top ranked 5 documents
        Map<Integer, Double> sorted = finalScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        writer.write("\n\nQuery Count " + lemmas.keySet().size());
        writer.write("\nVector Representation of Query ");
        writer.write("\n" + Arrays.toString(termQueryQt.keySet().toArray()));
        writer.write("\n" + Arrays.toString(termQueryQt.values().toArray()));
        int rank = 1;

        for (Integer docId : sorted.keySet()) {
            writer.write("\n\nRANK : " + rank + " DocId : " + docId + " score is " + sorted.get(docId));
            writer.write("\nDocument Headline is \"" + Indexing.getDocHeadLines().get(docId) + "\"");
            writer.write("\ncount of tokens in document : " + Indexing.getDocLemmaData().get(docId).size());
            TreeSet<String> finalVectorData = new TreeSet<>(Indexing.getDocLemmaData().get(docId).keySet());
            finalVectorData.addAll(lemmas.keySet());
            writer.write("\nCount after merging query and doc tokens " + finalVectorData.size());
            writer.write("\nVector Representation of Document ");


            TreeMap<String, Double> data = Indexing.getDocLemmaData().get(docId);
            writer.write("\n" + Arrays.toString(data.keySet().toArray()));
            Double[] res = new Double[finalVectorData.size()];
            Double[] docVector  = new Double[data.size()];
            int indexForDoc = 0;
            int index = 0;
            Double result = 0.0;
            Set<String> commmonWords = new TreeSet<>();
            for (String token : finalVectorData) {

                if (data.containsKey(token)) {
                    res[index] = data.get(token)/normalizedWt.get(docId);
                    docVector[indexForDoc] = res[index];
                    indexForDoc++;
                    if (lemmas.containsKey(token)) {
                        commmonWords.add(token);
//                        System.out.println("token " + token + " " + Indexing.getLemmaTFDict().get(token).getDf() + "  " + Indexing.getLemmaTFDict().get(token).getTf());
                    }
                }
                else {
                    res[index] = 0.0;
                }

                if (termQueryQt.containsKey(token)) {
                    result += termQueryQt.get(token) * res[index];
                }
                index++;
            }

            writer.write("\n" + Arrays.toString(docVector));
            writer.write("\nFor DocId " + docId + " Score by wt calculation: " + sorted.get(docId));
            writer.write("\nresult score by vector mulitiplication : " + result );
            rank++;
//            System.out.println("Document Headline " + Indexing.getDocHeadLines().get(docId));
//            System.out.println("\nDocId: " + docId + "  words : " + Arrays.toString(commmonWords.toArray()));
        }

    }



    public static Map<Integer, Double> calculateW1ForIndex() {

        long N = Indexing.getCollectionSize();
        Map<Integer, Double> normalizedWt = new HashMap<>();
        for (String token: Indexing.getLemmaDict().keySet()) {
            Set<PostingDoc> postings = Indexing.getLemmaDict().get(token);
            TermDFTF term = Indexing.getLemmaTFDict().get(token);
            for (PostingDoc  doc : postings) {
                DocumentPosting docData = Indexing.getDocLemmaPosting().get(doc.getDocId());
                double wt = getWt1(doc.getTf(), docData.getMaxTf(), term.getDf(), N);
                doc.setWt(wt);

                TreeMap<String, Double> data =  Indexing.getDocLemmaData().get(doc.getDocId());
                data.put(token, wt);
                Indexing.getDocLemmaData().put(doc.getDocId(), data);

                normalizedWt.put(doc.getDocId(), normalizedWt.getOrDefault(doc.getDocId(), 0.0) + (wt * wt));
            }
        }
        return normalizedWt;
    }

    public static Map<Integer, Double> calculateW2ForIndex() {

        long N = Indexing.getCollectionSize();
        Map<Integer, Double> normalizedWt = new HashMap<>();
        for (String token: Indexing.getLemmaDict().keySet()) {
            Set<PostingDoc> postings = Indexing.getLemmaDict().get(token);
            TermDFTF term = Indexing.getLemmaTFDict().get(token);
            for (PostingDoc  doc : postings) {
                DocumentPosting docData = Indexing.getDocLemmaPosting().get(doc.getDocId());
                //int tf, int doclen, int avgdoclen, int df, int N) {
                double wt = getWt2(doc.getTf(), docData.getDocLen(), Indexing.getSumOfDocLens()/N, term.getDf(), N);
                doc.setWt(wt);

                TreeMap<String, Double> data =  Indexing.getDocLemmaData().get(doc.getDocId());
                data.put(token, wt);
                Indexing.getDocLemmaData().put(doc.getDocId(), data);

                normalizedWt.put(doc.getDocId(), normalizedWt.getOrDefault(doc.getDocId(), 0.0) + (wt * wt));
            }
        }
        return normalizedWt;
    }

    public static void ProcessQuery(String query, String pathToOutDir, int queryNumber,
                                    Map<Integer, Double> normalizedWt, Map<Integer, Double> normalizedWt2) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(pathToOutDir + "/" + queryNumber));

        writer.write("Query " + queryNumber + ". " + query);

        //processing query to get vector representation of query
        writer.write("\n\nTop 5 Ranked Documents for W1");
        processQueryToGetVectorRepresentation(query, normalizedWt, 1, writer);


        //processing query to get vector representation of query
        writer.write("\n\n\nTop 5 Ranked Documents for W2");
        processQueryToGetVectorRepresentation(query, normalizedWt2, 2, writer);

        writer.close();
    }

    public static void main(String args[]) throws Exception {

        //creating index
        if (args.length < 4) {
            System.out.println("Incorrect Parameters ");
            System.exit(-1);
        }

        Indexing.runIndexing(args[0], args[1]);
        Map<Integer, Double> normalizedWt = calculateW1ForIndex();
        Map<Integer, Double> normalizedWt2 = calculateW2ForIndex();

        String pathToOutDir = args[2];
        File file = new File(args[3]);
        Scanner sc = new Scanner(file);
        String query = "";
        boolean flag = true;

        int queryCount = 1;
        while (sc.hasNextLine()) {
            String data = sc.nextLine();
            if (data.startsWith("Q") && data.endsWith(":")) {
                if (!flag) {
                    System.out.println("Processing Query " + queryCount + " " + query);
                    ProcessQuery(query, pathToOutDir, queryCount, normalizedWt, normalizedWt2);
                    queryCount++;
                    query = "";
                }
                else {
                    flag = false;
                }
            }
            else {
                if (!data.isEmpty()) {
                    query += " " + data;
                }
            }
        }
        System.out.println("Processing Query " + queryCount + " " + query);
        ProcessQuery(query, pathToOutDir, queryCount, normalizedWt, normalizedWt2);
    }
}
