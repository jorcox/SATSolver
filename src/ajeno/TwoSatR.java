package ajeno;

/******************************************************************************
 * File: TwoSat.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of a 2-SAT solver, which takes as input a boolean formula
 * in 2-CNF normal form (a conjunction of disjunctions with two literals per 
 * clause).  Unlike 3-SAT, which is known to be NP-complete and which is
 * suspected to admit no polynomial-time solutions, 2-SAT has a linear-time
 * solution.  This is the implementation used here.
 *
 * The key insight that makes 2-SAT solvable in polynomial-time is the fact
 * that a clause of the form (A or B) is equivalent to (~A implies B) AND (B
 * implies ~A).  Given this, we can convert any clause with two literals in it
 * into two directed implications.  This general construction allows us to
 * convert any 2-SAT formula into a graph of implications between all the
 * literals in the formulae and their negations.  We can then follow those
 * implications to see if that graph implies a logical impossibility.  In
 * particular, if in the graph of implications we find that A implies ~A and
 * ~A implies A, then we know that the formula is unsatisfiable.  If, however,
 * no node and its complement imply each other, we can show that the formula is
 * satisfiable.  The proof is as follows.  Begin by partioning the implication
 * graph into strongly-connected components.  If a literal and its complement
 * each imply each other, then they will be in the same connected component and
 * we can detect this efficiently.  If not, then each literal and its
 * complement must be in different connected components.  We can then decompose
 * the graph into a DAG of SCCs, then label the components of the SCC with
 * boolean values such that no SCC labeled true has an ancestor labeled false.
 * This involves some fun induction on the structure of the graph to prove.
 * However, if all that we're interested in is whether the input formula is
 * satisfiable, then we can do so by constructing the implication graph, 
 * finding its strongly connected components, then checking whether any node
 * and its negation are in the same SCC.  We can construct the implication
 * graph in O(n) time (where n is the number of formulae), and the graph has
 * O(n) nodes and O(n) edges.  Finding SCCs takes O(n) time using either
 * Tarjan's or Kosaraju's algorithm, and confirming that each node is in its
 * own SCC takes O(n) for a total runtime of O(n).
 *
 * This implementation relies on the existence of a Kosaraju class, also from
 * the Archive of Interesting Code.  You can find it online at
 *
 *         http://keithschwarz.com/interesting/code/?dir=kosaraju
 */
import java.util.*; // For List, Set

public final class TwoSatR {
    /**
     * Given as input a list of clauses representing a 2-CNF formula, returns
     * whether that formula is satisfiable.
     *
     * @param formula The input 2-CNF formula.
     * @return Whether the formula has a satisfying assignment.
     */
    public static <T> boolean isSatisfiable(List<Clause<T>> formula) {
        /* Begin by populating a set of all the variables in this formula. */
        Set<T> variables = new HashSet<T>();
        for (Clause<T> clause: formula) {
            variables.add(clause.first().value());
            variables.add(clause.second().value());
        }
          
        /* Construct the directed graph of implications.  Begin by creating the
         * nodes.
         */
        DirectedGraph<Literal<T>> implications = new DirectedGraph<Literal<T>>();
        for (T variable: variables) {
            /* Add both the variable and its negation. */
            implications.addNode(new Literal<T>(variable, true));
            implications.addNode(new Literal<T>(variable, false));
        }

        /* From each clause (A or B), add two clauses - (~A -> B) and (~B -> A)
         * to the graph as edges.
         */
        for (Clause<T> clause: formula) {
            implications.addEdge(clause.first().negation(), clause.second());
            implications.addEdge(clause.second().negation(), clause.first());
        }

        /* Compute the SCCs of this graph using Kosaraju's algorithm. */
        Map<Literal<T>, Integer> scc = Kosaraju.stronglyConnectedComponents(implications);

        /* Finally, check whether any literal and its negation are in the same
         * strongly connected component.
         */
        for (T variable: variables)
            if (scc.get(new Literal<T>(variable, true)).equals(scc.get(new Literal<T>(variable, false))))
                return false;

        /* If not, the formula must be satisfiable. */
        return true;
    }
}