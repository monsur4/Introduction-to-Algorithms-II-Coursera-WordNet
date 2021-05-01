import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private Digraph G;
    private int[] vertexTo;
    private int[] distTo;
    private boolean[] marked;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("You cannot have a null argument");
        }

        this.G = G;

        vertexTo = new int[G.V()];
        distTo = new int[G.V()];
        marked = new boolean[G.V()];

    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v >= G.V()) {
            throw new IllegalArgumentException("A vertex argument does not exist");
        }
        if (w < 0 || w >= G.V()) {
            throw new IllegalArgumentException("A vertex argument does not exist");
        }
        int lengthOfSAP = -1;
        int fromVertexV = 8;
        int fromVertexW = 9;

        if (v == w) {
            return 0;
        }

        initializeAllArrays();

        Queue<Integer> queue = new Queue<>();

        queue.enqueue(v);
        // edgeTo[v] -- not needed for the first vertices
        marked[v] = true;
        // vertexTo[v] = fromVertexV;
        distTo[v] = 0;
        queue.enqueue(w);
        marked[w] = true;
        // vertexTo[w] = fromVertexW;
        distTo[w] = 0;

        while (!queue.isEmpty()) {
            int exploringVertex = queue.dequeue();
            if (lengthOfSAP != -1 && distTo[exploringVertex] + 1 > lengthOfSAP) {
                return lengthOfSAP;
            }
            for (int vertex : G.adj(exploringVertex)) {
                if (!marked[vertex]) {
                    marked[vertex] = true;
                    vertexTo[vertex] = exploringVertex;
                    distTo[vertex] = distTo[exploringVertex] + 1;
                    queue.enqueue(vertex);
                }
                else {
                    if (vertexTo[vertex] != exploringVertex) {
                        int newSAP = distTo[vertex] + distTo[exploringVertex] + 1;
                        if (lengthOfSAP == -1) {
                            lengthOfSAP = newSAP;
                        }
                        else if (newSAP < lengthOfSAP) {
                            lengthOfSAP = newSAP;
                        }
                    }
                }
            }
        }
        return lengthOfSAP;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v >= G.V()) {
            throw new IllegalArgumentException("A vertex argument does not exist");
        }
        if (w < 0 || w >= G.V()) {
            throw new IllegalArgumentException("A vertex argument does not exist");
        }
        int lengthOfSAP = -1;
        int ancestorVertex = -1;
        int fromVertexV = 8;
        int fromVertexW = 9;

        if (v == w) {
            return v;
        }

        initializeAllArrays();

        Queue<Integer> queue = new Queue<>();

        queue.enqueue(v);
        // edgeTo[v] -- not needed for the first vertices
        marked[v] = true;
        // vertexTo[v] = fromVertexV;
        distTo[v] = 0;
        queue.enqueue(w);
        marked[w] = true;
        // vertexTo[w] = fromVertexW;
        distTo[w] = 0;

        while (!queue.isEmpty()) {
            int exploringVertex = queue.dequeue();
            if (lengthOfSAP != -1 && distTo[exploringVertex] + 1 > lengthOfSAP) {
                return ancestorVertex;
            }
            for (int vertex : G.adj(exploringVertex)) {
                if (!marked[vertex]) {
                    marked[vertex] = true;
                    vertexTo[vertex] = exploringVertex;
                    queue.enqueue(vertex);
                    distTo[vertex] = distTo[exploringVertex] + 1;
                }
                else {

                    if (vertexTo[vertex] != exploringVertex) {
                        int newSAP = distTo[vertex] + distTo[exploringVertex] + 1;
                        if (lengthOfSAP == -1) {
                            lengthOfSAP = newSAP;
                            ancestorVertex = vertex;
                        }
                        else if (newSAP < lengthOfSAP) {
                            lengthOfSAP = newSAP;
                            ancestorVertex = vertex;
                        }
                    }
                }
            }
        }

        return ancestorVertex;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("You cannot have a null argument");
        }
        for (int item : v) {
            // if (item == null) {
            //     throw new IllegalArgumentException("You cannot have a null argument");
            // }
            if (item < 0 || item >= G.V()) {
                throw new IllegalArgumentException("A vertex argument does not exist");
            }
        }
        for (int item : w) {
            // if (item == null) {
            //     throw new IllegalArgumentException("You cannot have a null argument");
            // }
            if (item < 0 || item >= G.V()) {
                throw new IllegalArgumentException("A vertex argument does not exist");
            }
        }

        int lengthOfSAP = -1;
        int fromVertexV = 8;
        int fromVertexW = 9;

        initializeAllArrays();

        Queue<Integer> queue = new Queue<>();
        for (int vertex : v) {
            queue.enqueue(vertex);
            // edgeTo[v] -- not needed for the first vertices
            marked[vertex] = true;
            vertexTo[vertex] = fromVertexV;
            distTo[vertex] = 0;
        }
        for (int vertex : w) {
            queue.enqueue(vertex);
            // edgeTo[v] -- not needed for the first vertices
            marked[vertex] = true;
            vertexTo[vertex] = fromVertexW;
            distTo[vertex] = 0;
        }

        while (!queue.isEmpty()) {
            int exploringVertex = queue.dequeue();
            if (lengthOfSAP != -1 && distTo[exploringVertex] + 1 > lengthOfSAP) {
                return lengthOfSAP;
            }
            Iterable<Integer> adjacentVertices = G.adj(exploringVertex);
            for (int vertex : adjacentVertices) {
                if (!marked[vertex]) {
                    marked[vertex] = true;
                    vertexTo[vertex] = vertexTo[exploringVertex];
                    distTo[vertex] = distTo[exploringVertex] + 1;
                    queue.enqueue(vertex);
                }
                else {
                    if (vertexTo[vertex] != vertexTo[exploringVertex]) {
                        int newSAP = distTo[vertex] + distTo[exploringVertex] + 1;
                        if (lengthOfSAP == -1) {
                            lengthOfSAP = newSAP;
                        }
                        else if (newSAP < lengthOfSAP) {
                            lengthOfSAP = newSAP;
                        }
                    }
                }
            }
        }

        return lengthOfSAP;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("You cannot have a null argument");
        }
        for (Integer item : v) {
            // if (item == null) {
            //     throw new IllegalArgumentException("You cannot have a null argument");
            // }
            if (item < 0 || item >= G.V()) {
                throw new IllegalArgumentException("A vertex argument does not exist");
            }
        }
        for (Integer item : w) {
            // if (item == null) {
            //     throw new IllegalArgumentException("You cannot have a null argument");
            // }
            if (item < 0 || item >= G.V()) {
                throw new IllegalArgumentException("A vertex argument does not exist");
            }
        }
        int lengthOfSAP = -1;
        int ancestorVertex = -1;
        int fromVertexV = 8;
        int fromVertexW = 9;

        initializeAllArrays();

        Queue<Integer> queue = new Queue<>();
        for (int vertex : v) {
            queue.enqueue(vertex);
            // edgeTo[v] -- not needed for the first vertices
            marked[vertex] = true;
            vertexTo[vertex] = fromVertexV;
            distTo[vertex] = 0;
        }
        for (int vertex : w) {
            queue.enqueue(vertex);
            // edgeTo[v] -- not needed for the first vertices
            marked[vertex] = true;
            vertexTo[vertex] = fromVertexW;
            distTo[vertex] = 0;
        }


        while (!queue.isEmpty()) {
            int exploringVertex = queue.dequeue();
            if (lengthOfSAP != -1 && distTo[exploringVertex] + 1 > lengthOfSAP) {
                return ancestorVertex;
            }
            Iterable<Integer> adjacentVertices = G.adj(exploringVertex);
            for (int vertex : adjacentVertices) {
                if (!marked[vertex]) {
                    marked[vertex] = true;
                    vertexTo[vertex] = vertexTo[exploringVertex];
                    distTo[vertex] = distTo[exploringVertex] + 1;
                    queue.enqueue(vertex);
                }
                else {
                    if (vertexTo[vertex] != vertexTo[exploringVertex]) {
                        int newSAP = distTo[vertex] + distTo[exploringVertex] + 1;
                        if (lengthOfSAP == -1) {
                            lengthOfSAP = newSAP;
                            ancestorVertex = vertex;
                        }
                        else if (newSAP < lengthOfSAP) {
                            lengthOfSAP = newSAP;
                            ancestorVertex = vertex;
                        }
                    }
                }
            }
        }

        return ancestorVertex;
    }

    private void initializeAllArrays() {
        vertexTo = new int[G.V()];
        distTo = new int[G.V()];
        marked = new boolean[G.V()];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
