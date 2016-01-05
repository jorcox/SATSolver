package utils;

import java.util.ArrayList;

import aima.core.logic.propositional.kb.data.Clause;
import aima.core.logic.propositional.kb.data.Literal;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;

public class Sentence {
	
	private ArrayList<Clause> clauses;
	
	
	public Sentence(ArrayList<Clause> entry){
		clauses = entry;
	}
	
	public boolean checkTwoSat(){
		for(Clause clausula : clauses){
			if(clausula.getLiterals().size() > 2)
				return false;
		}		
		return true;
	}
	
	public boolean checkHornSat(){
		for(Clause clausula : clauses){
			if(!clausula.isHornClause())
				return false;
		}		
		return true;
	}
	
	public ArrayList<PropositionSymbol> getUniqueSymbols(){
		ArrayList<PropositionSymbol> symbolArray = new ArrayList<PropositionSymbol>();
		for (Clause clause : clauses) {
			for (Literal lit : clause.getLiterals()) {
				if(!symbolArray.contains(lit.getAtomicSentence())){
					symbolArray.add(lit.getAtomicSentence());
				}				
			}			
		}
		return symbolArray;
	}

	public ArrayList<Clause> getClauses() {
		return clauses;
	}
	
	

}
