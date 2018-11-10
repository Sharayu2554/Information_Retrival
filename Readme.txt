Pre-Requisite:
1. resourcesIR Folder contains Stanford NLP Package which is used while running this program.
   Jars in that folder need to imported in External Libraries of the code
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2-javadoc.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2-models.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/stanford-corenlp-3.9.2-sources.jar
   ./resourcesIR/stanford-corenlp-full-2018-10-05/xom-1.2.10-src.jar
   Above libraries need to be added in Extrenal Lib module of your editor, if you are compiling code in editor. If  you are running jar, its not needed.

 2. Run
	java -jar ./HW3.jar ./Data/Cranfield ./Data/resourcesIR/stopwords ./Output_Retrival_Final/   ./Data/Queries/hw3.queries

	java -jar ./HW3.jar __PATH_TO_CranfieldDataSet__ ./resourcesIR/stopwords ./OutPut_Retrival/  ./Queriees/hw3.queries

3. Description file containa the alogrithm and description of code

4. Additional_result : answers to question 3, 4, 5, 6

5. OutPut_Retrival folder contains files, eacch file is the result of each query from hw3.queries file and questions 1, 2 are answered for each queries in those files.
	
