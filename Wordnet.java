import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

import java.util.ArrayList;
import java.util.Arrays;

public class WordNet {

    private int wordCount;
    private int numberOfSynsets;
    private final ST<String, ArrayList<Integer>> wordsTable;
    private final ST<Integer, String> wordsSynsetTable;
    private Digraph digraph;


    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) {
            throw new IllegalArgumentException("An argument to the constructor is null");
        }
        if (hypernyms == null) {
            throw new IllegalArgumentException("An argument to the constructor is null");
        }

        wordsTable = new ST<>();
        wordsSynsetTable = new ST<>();

        // read the synsets file and store all the words
        readSynset(synsets);

        // build the DAG using the hypernym file
        constructGraphFromHypernym(hypernyms);

        // check if constructed graph contains a cycle
        if (digraphHasCycle(digraph)) {
            throw new IllegalArgumentException(
                    "An argument to the constructor does not correspond to a rooted DAG");
        }
    }

    private void readSynset(String synsets) {
        In in = new In(synsets);
        String line = in.readLine();
        numberOfSynsets = 0;
        while (line != null) {
            numberOfSynsets++;

            String[] word_line_array = line.split(",");
            int word_id = Integer.parseInt(word_line_array[0]);
            String word_synonym_set = word_line_array[1];
            // insert synset into the word_synset_table
            wordsSynsetTable.put(word_id, word_synonym_set);

            String[] word_synonyms = word_synonym_set.split(" ");
            for (String word : word_synonyms) {
                if (wordsTable.contains(word)) {
                    ArrayList<Integer> word_associated_ids = wordsTable.get(word);
                    word_associated_ids.add(word_id);
                }
                else {
                    // initialize the array list with an estimate of number of times a word exists
                    ArrayList<Integer> word_associated_ids = new ArrayList<Integer>(5);
                    word_associated_ids.add(word_id);
                    wordsTable.put(word, word_associated_ids);
                    // if the word is not already in the symbol table, that means this is a new word
                    // increment word count
                    wordCount++;
                }
            }
            line = in.readLine();
        }
    }

    private void constructGraphFromHypernym(String hypernyms) {
        digraph = new Digraph(numberOfSynsets);
        In in_hypernym = new In(hypernyms);
        String hypernym_line = in_hypernym.readLine();
        while (hypernym_line != null) {
            String[] hypernym_line_array = hypernym_line.split(",");
            int synset_id = Integer.parseInt(hypernym_line_array[0]);
            for (int i = 1; i < hypernym_line_array.length; i++) {
                int hypernym_ids = Integer.parseInt(hypernym_line_array[i]);
                digraph.addEdge(synset_id, hypernym_ids);
            }
            hypernym_line = in_hypernym.readLine();
        }
    }

    private boolean digraphHasCycle(Digraph digraph) {
        DirectedCycle directedCycle = new DirectedCycle(digraph);
        return directedCycle.hasCycle();
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return wordsTable.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("An argument to the method is null");
        }
        String[] words_array = new String[wordCount];
        int i = 0;
        for (String word_in_queue : wordsTable.keys()) {
            words_array[i] = word_in_queue;
            i++;
        }

        //First sort the words array because some of the words may be unorder especially around synonyms
        Arrays.sort(words_array);

        // Run a binary search algorithm to determine if the word is a wordnet word
        int low = 0;
        int high = wordCount - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int result = word.compareTo(words_array[mid]);
            if (result > 0) {
                low = mid + 1;
            }
            else if (result == 0) {
                return true;
            }
            else {//if result<0
                high = mid - 1;
            }
        }
        return false;

    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null) {
            throw new IllegalArgumentException("An argument to the method is null");
        }
        if (nounB == null) {
            throw new IllegalArgumentException("An argument to the method is null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("An input noun is not a WordNet Noun");
        }
        boolean[] markedA = new boolean[numberOfSynsets];
        int[] distToA = new int[numberOfSynsets];
        boolean[] markedB = new boolean[numberOfSynsets];
        int[] distToB = new int[numberOfSynsets];

        Queue<Integer> queueA = new Queue<>();
        Queue<Integer> queueB = new Queue<>();

        // enque the synsets of nounA
        // this code considers that, there might be multiple synsets associated with a single word
        for (int synsetId : wordsTable.get(nounA)) {
            queueA.enqueue(synsetId);
            markedA[synsetId] = true;
            distToA[synsetId] = 0;
        }

        // enque the synset of nounB
        for (int synsetId : wordsTable.get(nounB)) {
            queueB.enqueue(synsetId);
            markedB[synsetId] = true;
            distToB[synsetId] = 0;
        }

        while (!queueA.isEmpty() || !queueB.isEmpty()) {
            if (!queueA.isEmpty()) {
                int numA = queueA.dequeue();
                if (markedB[numA]) {
                    return distToA[numA] + distToB[numA];
                }
                for (int element : digraph.adj(numA)) {
                    if (!markedA[element]) {
                        queueA.enqueue(element);
                        markedA[element] = true;
                        distToA[element] = distToA[numA] + 1;
                    }
                }
            }

            if (!queueB.isEmpty()) {
                int numB = queueB.dequeue();
                if (markedA[numB]) {
                    return distToA[numB] + distToB[numB];
                }
                for (int element : digraph.adj(numB)) {
                    if (!markedB[element]) {
                        queueB.enqueue(element);
                        markedB[element] = true;
                        distToB[element] = distToB[numB] + 1;
                    }
                }
            }
        }
        return -1;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null) {
            throw new IllegalArgumentException("An argument to the method is null");
        }
        if (nounB == null) {
            throw new IllegalArgumentException("An argument to the method is null");
        }

        boolean[] markedA = new boolean[numberOfSynsets];
        boolean[] markedB = new boolean[numberOfSynsets];

        Queue<Integer> queueA = new Queue<>();
        Queue<Integer> queueB = new Queue<>();
        // enque the synsets of nounA
        // this code considers that, there might be multiple synsets associated with a single word
        for (int synsetId : wordsTable.get(nounA)) {
            queueA.enqueue(synsetId);
            markedA[synsetId] = true;
        }
        // enque the synset of nounB
        for (int synsetId : wordsTable.get(nounB)) {
            queueB.enqueue(synsetId);
            markedB[synsetId] = true;
        }

        while (!queueA.isEmpty() || !queueB.isEmpty()) {
            if (!queueA.isEmpty()) {
                int numA = queueA.dequeue();
                if (markedB[numA]) {
                    return wordsSynsetTable.get(numA);
                }
                for (int element : digraph.adj(numA)) {
                    if (!markedA[element]) {
                        queueA.enqueue(element);
                        markedA[element] = true;
                    }
                }
            }

            if (!queueB.isEmpty()) {
                int numB = queueB.dequeue();
                if (markedA[numB]) {
                    return wordsSynsetTable.get(numB);
                }
                for (int element : digraph.adj(numB)) {
                    if (!markedB[element]) {
                        queueB.enqueue(element);
                        markedB[element] = true;
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {

        WordNet TestWordNet = new WordNet(args[0], args[1]);
        // System.out.println(TestWordNet.sap("o", "j"));
        System.out.println(TestWordNet.isNoun("horse"));
        System.out.println(TestWordNet.isNoun("ffgdgdfbtr"));
        System.out.println(TestWordNet.distance("horse", "cat"));
        System.out.println(TestWordNet.distance("horse", "bear"));
        System.out.println(TestWordNet.distance("horse", "table"));

        System.out.println(TestWordNet.sap("table", "table"));
        System.out.println(TestWordNet.sap("zebra", "table"));
        System.out.println(TestWordNet.sap("cat", "table"));
        
    }
}
