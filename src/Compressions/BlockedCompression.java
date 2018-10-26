package Compressions;

import DataModels.*;
import TestNLPPackage.TermDFTF;

import java.util.*;

public class BlockedCompression {
    private static int K = 8;

    public Map<String, List<BlockedCompressionObject>>  compressedIndex(Map<String, BlockedCompressionObject> dict) {
        Map<String, List<BlockedCompressionObject>> compressedData = new TreeMap<>();
        int k = 0;
        String term = "";
        List<BlockedCompressionObject> data = new LinkedList<>();
        for (String key : dict.keySet() ) {
            if (k > 7) {
                compressedData.put(term, data);
                System.out.println("Term " + term + " data " + Arrays.toString(data.toArray()));
                k = 0;
                term = "";
                data = new LinkedList<>();
            }
            term = term + key.length() + key;
            data.add(dict.get(key));
            k++;
        }
        return compressedData;
    }

    public Map<String, BlockedCompressionObject> blockCompression(Map<String, TreeSet<Integer>> dict,  //Integer posting list
                                                                  Map<String, TermDFTF> doc,  //term data
                                                                  Map<Integer, DocumentPosting> docPosting //doc metadata
    ) {
        Map<String, BlockedCompressionObject> dictionary = new HashMap<>();
        Map<Integer, CompressedDocumentData> docData = new HashMap<>();

        System.out.println("Document Metdata ");
        //get compressedDocumentData
        for (Integer docId : docPosting.keySet()) {
            DocumentPosting docMetadata = docPosting.get(docId);
            docData.put(docId, new CompressedDocumentData(Codes.gammaCode(docMetadata.getMaxTf()), Codes.gammaCode(docMetadata.getDocLen())));
            System.out.println("DocId : " + docId + " " + docData.get(docId));
        }

        //get blockedCompression object for each term
        for (String term : dict.keySet()) {
            Set<Integer> list = dict.get(term);
            boolean flag = true;
            int prev = 0;

            List<CompressedDocPosting> postingList = new LinkedList<>();
            //get gaps and compressed doc posting
            for (Integer docId : list) {
                if (flag) {
                    prev = docId;
                    postingList.add(new CompressedDocPosting(Codes.gammaCode(docId), docData.get(docId)));
                    flag = false;
                    continue;
                }
                postingList.add(new CompressedDocPosting(Codes.gammaCode(docId - prev), docData.get(docId)));
                prev = docId;
            }
            TermDFTF termData = doc.get(term);
            dictionary.put(
                    term, new BlockedCompressionObject(
                            Codes.gammaCode(termData.getDf()),
                            Codes.gammaCode(termData.getTf()),
                            postingList)
            );
            System.out.println(term + " " + dictionary.get(term));
        }
        System.out.println(dictionary.size());
        return dictionary;
    }

    public Map<String, BlockedCompressionObjectInt> blockCompressionInt(Map<String, TreeSet<Integer>> dict,  //Integer posting list
                                                                  Map<String, TermDFTF> doc,  //term data
                                                                  Map<Integer, DocumentPosting> docPosting //doc metadata
    ) {
        Map<String, BlockedCompressionObjectInt> dictionary = new HashMap<>();
        Map<Integer, CompressedDocumentDataInt> docData = new HashMap<>();

        //get compressedDocumentData
        for (Integer docId : docPosting.keySet()) {
            DocumentPosting docMetadata = docPosting.get(docId);
            docData.put(docId, new CompressedDocumentDataInt(docMetadata.getMaxTf(), docMetadata.getDocLen()));
        }

        //get blockedCompression object for each term
        for (String term : dict.keySet()) {
            Set<Integer> list = dict.get(term);
            boolean flag = true;
            int prev = 0;

            List<CompressedDocPostingInt> postingList = new LinkedList<>();
            //get gaps and compressed doc posting
            for (Integer docId : list) {
                if (flag) {
                    prev = docId;
                    postingList.add(new CompressedDocPostingInt(Codes.gammaCode(docId), docData.get(docId)));
                    flag = false;
                    continue;
                }
                postingList.add(new CompressedDocPostingInt(Codes.gammaCode(docId - prev), docData.get(docId)));
                prev = docId;
            }
            TermDFTF termData = doc.get(term);
            dictionary.put(
                    term, new BlockedCompressionObjectInt(termData.getDf(), termData.getTf(), postingList)
            );
        }
        return dictionary;
    }

}
