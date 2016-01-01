package ajeno;
/******************************************************************************
 * File: Literal.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing a literal in a boolean expression.  A literal is either
 * a variable or its negation, and is represented as a pair of some value
 * representing a literal and a boolean flag indicating whether the value is
 * positive or its negation.
 */
public final class Literal<T> {
    private final T mValue;            // The variable in question
    private final boolean mIsPositive; // Whether this is X (true) or ~X (false)

    /**
     * Constructs a new literal from the specified value and sign.  Null
     * literals are not supported.
     *
     * @param value The value representing the literal.
     * @param isPositive Whether the value is positive or negative.
     */
    public Literal(T value, boolean isPositive) {
        /* Check that the value is indeed non-null. */
        if (value == null)
            throw new NullPointerException("Cannot use null literals.");

        mValue = value;
        mIsPositive = isPositive;
    }

    /**
     * Returns the negation of this literal.
     *
     * @return A Literal holding the negation of this literal.
     */
    public Literal<T> negation() {
        return new Literal<T>(value(), !isPositive());
    }

    /**
     * Returns the object used to represent the literal in this clause.
     *
     * @return The object used to represent the literal in this clause.
     */
    public T value() {
        return mValue;
    }

    /**
     * Returns whether this literal is positive or negative.
     *
     * @return Whether this literal is positive or negative.
     */
    public boolean isPositive() {
        return mIsPositive;
    }

    /**
     * Returns a string representation of this literal.
     *
     * @return A string representation of this literal.
     */
    @Override
    public String toString() {
        return (isPositive() ? "" : "~") + value();
    }

    /**
     * Returns whether this literal is equal to some other object.
     *
     * @param obj An object to which this literal should be compared.
     * @return Whether this object is equal to that object.
     */
    @Override
    public boolean equals(Object obj) {
        /* Confirm that the other object has the proper type. */
        if (!(obj instanceof Literal))
            return false;

        /* Downcast, then do a field-by-field comparison. */
        Literal<?> realObj = (Literal) obj;

        return realObj.isPositive() == isPositive() && realObj.value().equals(value());
    }

    /**
     * Returns a semi-random value computed from the state of this object.
     *
     * @return The object's hash code.
     */
    @Override
    public int hashCode() {
        return (isPositive() ? 1 : 31) * value().hashCode();
    }
}