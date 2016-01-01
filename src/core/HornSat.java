package core;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.util.datastructure.Pair;
import aima.core.logic.propositional.kb.data.Clause;
import aima.core.logic.propositional.kb.data.Literal;
import ajeno.TwoSat;

public class HornSat {
	

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
				ArrayList<Pair<Clause,Literal>> toRemoveLit = new ArrayList<Pair<Clause,Literal>>();
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
						//sentence.remove(clausula);
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
						//toRemoveLit.add(new Pair<Clause, Literal>(clausula,new Literal(litS.getAtomicSentence(),sim.isPositiveLiteral())));
						Set<Literal> oldLits = new LinkedHashSet<Literal>(clausula.getLiterals());
						Set<Literal> newLits = new LinkedHashSet<Literal>();
						for (Literal literal : oldLits) {
							Literal litToMove = new Literal(litS.getAtomicSentence(),sim.isNegativeLiteral());
							if(!literal.equals(litToMove)){
								newLits.add(literal);
							}
						}
						//int d = sentence.indexOf(clausula);
						sentence.remove(clausula);
						clausula = new Clause(newLits);
						sentence.add(i, clausula);
						//clausula.getLiterals().remove(new Literal(litS.getAtomicSentence(),sim.isPositiveLiteral()));
					}
				}
				
				for (Clause clause : toRemoveClause) {
					sentence.remove(clause);
				}
				
//				for(Pair<Clause, Literal> par : toRemoveLit){
//					Clause hola = new Clause(sentence.get(sentence.indexOf(par.getFirst())));
//					sentence.get(sentence.indexOf(par.getFirst())).getLiterals().remove(par.getSecond());
//				}
				
				
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
