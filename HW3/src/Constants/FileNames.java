package src.Constants;

public enum FileNames {

    //Storing lemma in uncompressed format
    //DocInfo file : (DocId, maxtf, docLength ) (4 bytes, 4 bytes, 4 bytes) (12 bytes for each doc * 1400 doc )(16.8k)
    //posting ptr : (docId, docId, docId) (4 bytes for each docId)
    //Index : (lemma of term , df, tf, posting pointer) fixed width(34 bytes, 4 bytes, 4 bytes, 4 bytes) (42 bytes for each term)
    INDEX_UNCOMPRESS_VERSION1_DOCINFO,
    INDEX_UNCOMPRESS_VERSION1_POSTING_PTR,
    INDEX_UNCOMPRESS_VERSION1_INDEX,


    //stemma
    //DocInfo file : (DocId, maxtf, docLength ) (4 bytes, 4 bytes, 4 bytes) (12 bytes for each doc * 1400 doc )(16.8k)
    //posting ptr : (docId, docId, docId) (4 bytes for each docId)
    //Index : (stemma of term , df, tf, posting pointer) fixed width(34 bytes, 4 bytes, 4 bytes, 4 bytes) (42 bytes for each term)
    INDEX_UNCOMPRESS_VERSION2_DOCINFO,
    INDEX_UNCOMPRESS_VERSION2_POSTING_PTR,
    INDEX_UNCOMPRESS_VERSION2_INDEX,


    //lemma block compression
    //DocInfo file : (DocId, maxtf, docLength ) (4 bytes, 4 bytes, 4 bytes) (12 bytes for each doc * 1400 doc )(16.8k)
    //Posting_ptr : variable length encoding (gammaCode for docId, gammacode for gap, gammaCode for gap) (variable length bytes)
    //Term : Block Compressed where k = 8 (1 byte for length , len bytes for term, 1 byte for length, length bytes for term , ..)
    //Index :  k = 8 (Term ptr, ((df, tf, posting ptr) (repeated 8 times)), 8th Term ptr, ((df, tf, posting ptr) (repeated 8 times)))
    //(4 bytes, (4 bytes, 4 bytes, 4 bytes) * 8) for one block of k = 8 (100 bytes for on block)
    INDEX_COMPRESS_VERSION1_DOCINFO,
    INDEX_COMPRESS_VERSION1_POSTING_PTR,
    INDEX_COMPRESS_VERSION1_TERM,
    INDEX_COMPRESS_VERSION1_INDEX,


    //stemma front coding
    //DocInfo file : (DocId, maxtf, docLength ) (4 bytes, 4 bytes, 4 bytes) (12 bytes for each doc * 1400 doc )(16.8k)
    //Posting_ptr : variable length encoding (delta code for docId, delta code for gap, delta code for gap, ...)
    //Term : Front Coding with k = 8,
    //(1 byte for length, longest common prefix for next 8 terms, *, rest of first term, 1 byte for length, #, next term without prefix, ...)
    //[ae, aerodynamic, aeroplane, ae00 ] = for k = 3 [ 2ae*9#rodynamic7#roplane][..]
    //Index : k = 8 (Term ptr, ((df, tf, posting ptr) (repeated 8 times)), 8th Term ptr, ((df, tf, posting ptr) (repeated 8 times)))
    //(4 bytes, (4 bytes, 4 bytes, 4 bytes) * 8) for one block of k = 8 (100 bytes for on block)
    INDEX_COMPRESS_VERSION2_DOCINFO,
    INDEX_COMPRESS_VERSION2_POSTING_PTR,
    INDEX_COMPRESS_VERSION2_TERM,
    INDEX_COMPRESS_VERSION2_INDEX

}
