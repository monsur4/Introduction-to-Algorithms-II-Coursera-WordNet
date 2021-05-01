import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private WordNet wordNet;
    BreadthFirstDirectedPaths breadthFirstDirectedPaths;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        int maxDistance = 0;
        String outcast = " ";

        for (String noun : nouns) {
            int nounTotalDistance = 0;
            for (int i = 0; i < nouns.length; i++) {
                nounTotalDistance += wordNet.distance(noun, nouns[i]);
            }
            if (nounTotalDistance > maxDistance) {
                maxDistance = nounTotalDistance;
                outcast = noun;
            }
        }
        return outcast;


    }

    // test client
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
