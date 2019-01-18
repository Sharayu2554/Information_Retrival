Pre-Requisite:
1. resourcesIR Folder contains Stanford NLP Package which is used while running this program.
   Jars in that folder need to imported in External Libraries of the code
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2-javadoc.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2-models.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2-sources.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/xom-1.2.10-src.jar
   Above libraries need to be added in Extrenal Lib module of your editor

 2. Run
	java -jar ./HW2.jar ./Data/Cranfield ./resourcesIR/stopwords ./OutPut/

	java -jar ./HW2.jar __PATH_TO_CranfieldDataSet__ ./resourcesIR/stopwords ./OutPut/
	
	or 
	
	java src/Indexing.java __PATH_TO_CranfieldDataSet__ ./resourcesIR/stopwords ./OutPut/

3. Description file containa the alogrithm and description of what output file contains

4. Additional_result : contains output of program to answers the questions of additional points

5. OutPut folder contains all the output files for index1, index2, compressed and uncompressed


