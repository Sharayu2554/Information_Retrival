wget http://nlp.stanford.edu/software/stanford-corenlp-full-2018-10-05.zip

unzip stanford-corenlp-full-2018-10-05.zip

cd stanford-corenlp-full-2018-10-05

for file in `find ./ -name "*.jar"`; do export; CLASSPATH="$CLASSPATH:`realpath $file`"; done

cd ../

javac src/IRUtilies/*

javac src/Constants/*

javac src/DataModels/*

javac src/*

javac src/Indexing.java

javac src/Retrival.java

java src.Retrival /path/to/cranfield/dataset /path/to/stopwords/file /path/to/output/folder /path/to/query/file

java src.Retrival __PATH__TO__CRANFILED_FOLDER__ ./resourcesIR/stopwords ./Output_Retrival/ ./Queries/hw3.queries



If you donot want to download zip and run you candirectly run the jar:
2. Run
	java -jar ./HW3.jar ./Data/Cranfield ./Data/resourcesIR/stopwords ./Output_Retrival_Final/   ./Data/Queries/hw3.queries

	java -jar ./HW3.jar __PATH_TO_CranfieldDataSet__ ./resourcesIR/stopwords ./OutPut_Retrival/  ./Queriees/hw3.queries



3. Description file containa the alogrithm and description of code

4. Additional_result : answers to question 3, 4, 5, 6

5. OutPut_Retrival folder contains files, eacch file is the result of each query from hw3.queries file and questions 1, 2 are answered for each queries in those files.

