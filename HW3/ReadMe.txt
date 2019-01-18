source /usr/local/corenlp350/classpath.sh 

javac -classpath $CLASSPATH  src/IRUtilies/*

javac -classpath $CLASSPATH  src/Constants/*

javac -classpath $CLASSPATH src/DataModels/*

javac -classpath $CLASSPATH  src/Indexing.java

javac -classpath $CLASSPATH src/Retrival.java

###java src.Retrival /path/to/cranfield/dataset /path/to/stopwords/file /path/to/output/folder /path/to/query/file

java src.Retrival /people/cs/s/sanda/cs6322/Cranfield ./resourcesIR/stopwords ./OutPut_Retrival/ ./Queries/hw3.queries


3. Description file containa the alogrithm and description of code

4. Additional_result : answers to question 3, 4, 5, 6

5. OutPut_Retrival folder contains files, eacch file is the result of each query from hw3.queries file and questions 1, 2 are answered for each queries in those files.

