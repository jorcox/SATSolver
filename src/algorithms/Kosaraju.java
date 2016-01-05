package algorithms;

/******************************************************************************
 * File: Kosaraju.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of Kosaraju's algorithm for finding strongly-connected
 * components (SCCs) of a graph in linear time.  While Kosaraju's algorithm is
 * not the fastest known algorithm for computing SCCs (both Tarjan's algorithm
 * and Gabow's algorithm are faster by roughly a factor of two), it is a
 * remarkably elegant algorithm with asymptotic behavior identical to the other
 * algorithms.
 *
 * Kosaraju's algorithm relies on several important properties of graphs.
 * First, any graph can be decomposed into a directed acyclic graph (DAG) of
 * SCCs.  This yields an interesting observation.  Suppose that we start in a
 * node in an SCC that's a sink node in this DAG and perform a depth-first
 * search.  Because the SCC is a sink in the DAG, there are no outgoing edges
 * from it, and so every node we encounter we must be in the same SCC.  If we
 * then remove all of these nodes and repeat, we can repeatedly find sink SCCs,
 * run depth-first searches in them, and then recover all the nodes in the SCC.
 *
 * The challenge is how we can find which SCCs are sinks in the DAG.  For this,
 * Kosaraju's algorithm uses a clever trick.  Suppose that we run a full depth-
 * first search on the nodes in the graph, recording each node on a stack when
 * we finish expanding it.  Then we know that the topmost node of this stack
 * must be in a source SCC, since if it weren't, then some node in an SCC above
 * it would be have been expanded before it.  More generally, if two nodes u
 * and v are on the stack with u deeper than v, then the SCC containing v is
 * no lower than the SCC containing u.  This means that if we run this DFS in
 * the graph and then explore the nodes in the order in which they come back
 * off the stack, we'll explore the DAG's sources one after the other.
 *
 * Of course, this is backwards - we want to explore the sinks, not the
 * sources!  But this isn't a problem.  If we consider the reverse graph of
 * the original graph (formed by directing each arc the opposite of its normal
 * direction), then we don't change the SCCs.  You can see this by realizing
 * that two nodes in an SCC have two paths between them - one forward and one
 * backward - and so in the reverse graph both of these paths will still exist,
 * albeit swapped with one another.  However, reversing the edges does change
 * the DAG of SCCs in the graph by inverting all of the arcs in it.  This means
 * that in the reverse graph source SCCs become sink SCCs and vice-versa.
 * The net result of this is that if we run a full DFS in the reverse graph,
 * then run a depth-first search from each node in the graph in the reverse
 * order in which they are visited, we will end up recovering all the SCCs.
 */
import java.util.*; // For ArrayList, HashSet, HashMap

import utils.DirectedGraph;

public final class Kosaraju {
    /**
     * Given a directed graph, returns a map from the nodes in that graph to
     * integers representing the strongly connected components.  Each node's 
     * number will be the same as the numbers of each other node in its 
     * strongly connected component.  The enumeration will start at zero and
     * increase by one for each strongly connected component.
     *
     * @param g A directed graph.
     * @return A map from the nodes of that graph to integers indicating to 
     *         which connected component they belong.
     */
    public static <T> Map<T, Integer> stronglyConnectedComponents(DirectedGraph<T> g) {
        /* Run a depth-first search in the reverse graph to get the order in
         * which the nodes should be processed.
         */
        Stack<T> visitOrder = dfsVisitOrder(graphReverse(g));

        /* Now we can start listing connected components.  To do this, we'll
         * create the result map, as well as a counter keeping track of which
         * DFS iteration this is.
         */
        Map<T, Integer> result = new HashMap<T, Integer>();
        int iteration = 0;

        /* Continuously process the the nodes from the queue by running a DFS
         * from each unmarked node we encounter.
         */
        while (!visitOrder.isEmpty()) {
            /* Grab the last node.  If we've already labeled it, skip it and
             * move on.
             */
            T startPoint = visitOrder.pop();
            if (result.containsKey(startPoint))
                continue;

            /* Run a DFS from this node, recording everything we visit as being
             * at the current level.
             */
            markReachableNodes(startPoint, g, result, iteration);

            /* Bump up the number of the next SCC to label. */
            ++iteration;
        }

        return result;
    }

    /**
     * Given a directed graph, returns the reverse of that graph.
     *
     * @param g The graph to reverse.
     * @return The reverse graph.
     */
    private static <T> DirectedGraph<T> graphReverse(DirectedGraph<T> g) {
        DirectedGraph<T> result = new DirectedGraph<T>();

        /* Copy over the nodes. */
        for (T node: g)
            result.addNode(node);

        /* Flip all the edges. */
        for (T node: g)
            for (T endpoint: g.edgesFrom(node))
                result.addEdge(endpoint, node);
        
        return result;
    }

    /**
     * Given a graph, returns a queue containing the nodes of that graph in 
     * the order in which a DFS of that graph finishes expanding the nodes.
     *
     * @param g The graph to explore.
     * @return A stack of nodes in the order in which the DFS finished
     *         exploring them.
     */
    private static <T> Stack<T> dfsVisitOrder(DirectedGraph<T> g) {
        /* The resulting ordering of the nodes. */
        Stack<T> result = new Stack<T>();

        /* The set of nodes that we've visited so far. */
        Set<T> visited = new HashSet<T>();

        /* Fire off a DFS from each node. */
        for (T node: g)
            recExplore(node, g, result, visited);

        return result;
    }

    /**
     * Recursively explores the given node with a DFS, adding it to the output
     * list once the exploration is complete.
     *
     * @param node The node to start from.
     * @param g The graph to explore.
     * @param result The final listing of the node ordering.
     * @param visited The set of nodes that have been visited so far.
     */
    private static <T> void recExplore(T node, DirectedGraph<T> g,
                                       Stack<T> result, Set<T> visited) {
        /* If we've already been at this node, don't explore it again. */
        if (visited.contains(node)) return;

        /* Otherwise, mark that we've been here. */
        visited.add(node);

        /* Recursively explore all the node's children. */
        for (T endpoint: g.edgesFrom(node))
            recExplore(endpoint, g, result, visited);

        /* We're done exploring this node, so add it to the list of visited
         * nodes.
         */
        result.push(node);
    }

    /**
     * Recursively marks all nodes reachable from the given node by a DFS with
     * the current label.
     *
     * @param node The starting point of the search.
     * @param g The graph in which to run the search.
     * @param result A map in which to associate nodes with labels.
     * @param label The label that we should assign each node in this SCC.
     */
    private static <T> void markReachableNodes(T node, DirectedGraph<T> g,
                                               Map<T, Integer> result,
                                               int label) {
        /* If we've visited this node before, stop the search. */
        if (result.containsKey(node)) return;

        /* Otherwise label the node with the current label, since it's
         * trivially reachable from itself.
         */
        result.put(node, label);

        /* Explore all nodes reachable from here. */
        for (T endpoint: g.edgesFrom(node))
            markReachableNodes(endpoint, g, result, label);
    }
}