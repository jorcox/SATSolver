package algorithms;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import utils.*;

public class HornSat {
	
	/**
	 * Comprueba si la sentencia de Horn es satisfacible.
	 */
	public static boolean isSatisfiable(ArrayList<Clause> sentence) {
		boolean hasSimple = true;
		while (hasSimple) {
			/*
			 * Comprobacion de la existencia de una clausula simple
			 */
			hasSimple = false;
			Clause simpleClause = null;
			for (Clause clausula : sentence) {
				if (clausula.isUnitClause()) {
					hasSimple = true;
					simpleClause = clausula;
					break;
				}
			}

			if (hasSimple) {
				ArrayList<Clause> toRemoveClause = new ArrayList<Clause>();
				Literal sim = simpleClause.getLiterals().iterator().next();
				
				for (int i = 0; i < sentence.size(); i++) {
					Clause clausula = sentence.get(i);					
					Literal litS = getUnitLiteral(clausula, sim);
					
				    /*
					 * Si existe en esta clausula y tienen el mismo valor
					 */
					if (litS != null && (litS.equals(sim))) {
						toRemoveClause.add(clausula);
					}
					
					/*
					 * Caso de insatisfabilidad
					 */
					else if (litS != null && (!litS.equals(sim)) && (clausula.getLiterals().size() == 1)) {
						return false;
					}
					
					/*
					 * Si existe en esta clausula y tienen distinto valor
					 */
					else if (litS != null && (!litS.equals(sim))) {
						Set<Literal> oldLits = new LinkedHashSet<Literal>(clausula.getLiterals());
						Set<Literal> newLits = new LinkedHashSet<Literal>();
						for (Literal literal : oldLits) {
							Literal litToMove = new Literal(litS.getAtomicSentence(),sim.isNegativeLiteral());
							if(!literal.equals(litToMove)){
								newLits.add(literal);
							}
						}
						sentence.remove(clausula);
						clausula = new Clause(newLits);
						sentence.add(i, clausula);
					}
				}
				
				for (Clause clause : toRemoveClause) {
					sentence.remove(clause);
				}
				
			}
		}
		return true;
	}

	
	/*
	 * Si existe devuelve el literal dado de una clausula
	 * Si no existe devuelve null
	 */
	private static Literal getUnitLiteral(Clause clausula, Literal sim) {
		for (Literal lit : clausula.getLiterals()) {
			if(sim.getAtomicSentence().getSymbol().equals(lit.getAtomicSentence().getSymbol())){
				return lit;
			}
		}
		return null;
	}
}
