package ajeno;
/******************************************************************************
 * File: Clause.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing a clause in a 2SAT formula.  Each clause a disjunction
 * of two literals.
 */
public final class Clause<T> {
    private final Literal<T> mOne; // The two literals this clause is made of.
    private final Literal<T> mTwo;

    /**
     * Constructs a new clause out of two literals.
     *
     * @param one The first literal
     * @param two The second literal
     */
    public Clause(Literal<T> one, Literal<T> two) {
        mOne = one;
        mTwo = two;
    }

    /**
     * Returns the first literal in this clause.
     *
     * @return The first literal in this clause.
     */
    public Literal<T> first() {
        return mOne;
    }

    /**
     * Return the second literal in this clause.
     *
     * @return The second literal in this clause.
     */
    public Literal<T> second() {
        return mTwo;
    }

    /**
     * Returns a string representation of this clause.
     *
     * @return A string representation of this clause
     */
    @Override
    public String toString() {
        return "(" + first() + " or " + second() + ")";
    }
}