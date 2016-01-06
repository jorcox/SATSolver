package utils;

import java.util.ArrayList;

public class Sentence {
	
	private ArrayList<Clause> clauses;
	
	public Sentence(ArrayList<Clause> entry){
		clauses = entry;
	}
	
	/**
	 * Comprueba que la sentencia es 2-SAT
	 */
	public boolean checkTwoSat(){
		for(Clause clausula : clauses){
			if(clausula.getLiterals().size() > 2)
				return false;
		}		
		return true;
	}
	
	/**
	 * Comprueba que la sentencia es Horn-SAT
	 */
	public boolean checkHornSat(){
		for(Clause clausula : clauses){
			if(!clausula.isHornClause())
				return false;
		}		
		return true;
	}
	
	/**
	 * Obtiene un arraylist con los literales (sin repeticiones)
	 */
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
