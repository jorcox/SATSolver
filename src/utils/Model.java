package utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): pages 240, 245.<br>
 * <br>
 * Models are mathematical abstractions, each of which simply fixes the truth or
 * falsehood of every relevant sentence. In propositional logic, a model simply
 * fixes the <b>truth value</b> - <em>true</em> or <em>false</em> - for
 * every proposition symbol.<br>
 * <br>
 * Models as implemented here can represent partial assignments 
 * to the set of proposition symbols in a Knowledge Base (i.e. a partial model).
 * 
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class Model {

	private HashMap<PropositionSymbol, Boolean> assignments = new HashMap<PropositionSymbol, Boolean>();

	/**
	 * Default Constructor.
	 */
	public Model() {
	}

	public Model(Map<PropositionSymbol, Boolean> values) {
		assignments.putAll(values);
	}

	public Boolean getValue(PropositionSymbol symbol) {
		return assignments.get(symbol);
	}

	public boolean isTrue(PropositionSymbol symbol) {
		return Boolean.TRUE.equals(assignments.get(symbol));
	}

	public boolean isFalse(PropositionSymbol symbol) {
		return Boolean.FALSE.equals(assignments.get(symbol));
	}

	public Model union(PropositionSymbol symbol, boolean b) {
		Model m = new Model();
		m.assignments.putAll(this.assignments);
		m.assignments.put(symbol, b);
		return m;
	}
	
	public Model unionInPlace(PropositionSymbol symbol, boolean b) {
		assignments.put(symbol, b);
		return this;
	}
	
	public boolean remove(PropositionSymbol p) {
		return assignments.remove(p);
	}
	
	public Model flip(PropositionSymbol s) {
		if (isTrue(s)) {
			return union(s, false);
		}
		if (isFalse(s)) {
			return union(s, true);
		}
		return this;
	}

	public Set<PropositionSymbol> getAssignedSymbols() {
		return Collections.unmodifiableSet(assignments.keySet());
	}

	/**
	 * Determine if the model satisfies a set of clauses.
	 * 
	 * @param clauses
	 *            a set of propositional clauses.
	 * @return if the model satisfies the clauses, false otherwise.
	 */
	public boolean satisfies(Set<Clause> clauses) {
		for (Clause c : clauses) {
			// All must to be true
			if (!Boolean.TRUE.equals(determineValue(c))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine based on the current assignments within the model, whether a
	 * clause is known to be true, false, or unknown.
	 * 
	 * @param c
	 *            a propositional clause.
	 * @return true, if the clause is known to be true under the model's
	 *         assignments. false, if the clause is known to be false under the
	 *         model's assignments. null, if it is unknown whether the clause is
	 *         true or false under the model's current assignments.
	 */
	public Boolean determineValue(Clause c) {
		Boolean result = null; // i.e. unknown

		if (c.isTautology()) { // Test independent of the model's assignments.
			result = Boolean.TRUE;
		} else if (c.isFalse()) { // Test independent of the model's
									// assignments.
			result = Boolean.FALSE;
		} else {
			boolean unassignedSymbols = false;
			Boolean value             = null;
			for (PropositionSymbol positive : c.getPositiveSymbols()) {
				value = assignments.get(positive);
				if (value != null) {
					if (Boolean.TRUE.equals(value)) {
						result = Boolean.TRUE;
						break;
					}
				} else {
					unassignedSymbols = true;
				}
			}
			// If truth not determined, continue checking negative symbols
			if (result == null) {
				for (PropositionSymbol negative : c.getNegativeSymbols()) {
					value = assignments.get(negative);
					if (value != null) {
						if (Boolean.FALSE.equals(value)) {
							result = Boolean.TRUE;
							break;
						}
					} else {
						unassignedSymbols = true;
					}
				}

				if (result == null) {
					// If truth not determined and there are no
					// unassigned symbols then we can determine falsehood
					// (i.e. all of its literals are assigned false under the
					// model)
					if (!unassignedSymbols) {
						result = Boolean.FALSE;
					}
				}
			}
		}

		return result;
	}

	public void print() {
		for (Map.Entry<PropositionSymbol, Boolean> e : assignments.entrySet()) {
			System.out.print(e.getKey() + " = " + e.getValue() + " ");
		}
		System.out.println();
	}

	@Override
	public String toString() {
		return assignments.toString();
	}

	//
	// START-PLVisitor
	public Boolean visitPropositionSymbol(PropositionSymbol s, Boolean arg) {
		if (s.isAlwaysTrue()) {
			return Boolean.TRUE;
		}
		if (s.isAlwaysFalse()) {
			return Boolean.FALSE;
		}
		return getValue(s);
	}
	// END-PLVisitor
	//
}