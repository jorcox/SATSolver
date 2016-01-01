package core;

/******************************************************************************
 * Author: Jorge Cáncer Gil
 */

import java.util.ArrayList;
import ajeno.Literal;

public final class Clausula<T> {
    private ArrayList<Literal<T>> literals;

    /**
     * Constructs a new clause from a list of literals.
     *
     * @param literals The list of literals
     */
    public Clausula(ArrayList<Literal<T>> literals) {
        this.literals = literals;
    }

    /**
     * Returns the literal in the given index.
     *
     * @return  the literal in the given index.
     */
    public Literal<T> getLit(int index) {
        return literals.get(index);
    }
    
    /**
     * Returns true if this clause has only one literjarl
     * 
     * @return true if this clause has only one literjarl
     */
    public boolean isSimple() {
        return literals.size() == 1;
    }
    
    
    /**
     * 
     */
    public Literal<T> hasLiteral(Literal<T> lit) {
    	for (Literal<T> literal : literals) {
			if(literal.value().equals(lit.value())){
				return literal;
			}
		}
        return null;
    }
    
    /**
     * 
     */
    public void deleteLiteral(Literal<T> lit) {
    	Literal<T> delete = null;
    	for (Literal<T> literal : literals) {
			if(literal.value().equals(lit.value())){
				delete = literal;
				break;
			}
		}
    	if(delete != null){
    		literals.remove(delete);
    	}
    	
    }
    
    /**
     * 
     * @return
     */
    public int numLits(){
    	return literals.size();
    }
    

    /**
     * Returns a string representation of this clause.
     *
     * @return A string representation of this clause
     */
    @Override
    public String toString() {
    	String out = "(";
    	for (int i = 0; i < literals.size()-1; i++) {
			out += literals.get(i) + " or ";
		}
    	return out += literals.get(literals.size()-1) + ")";
    }

	public Literal<T> getSimple() {
		return literals.get(0);
	}
}