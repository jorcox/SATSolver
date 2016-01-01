package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ajeno.TwoSat;

import aima.core.logic.propositional.kb.data.Clause;
import aima.core.logic.propositional.kb.data.Literal;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;

public class SATSolver {

	public static void main(String[] args) {
		
		String type ="2-SAT";
		
		/*
		 * Lectura del fichero
		 */
		String ficheroSAT = args[0];
		File file = new File(ficheroSAT);
		try {
			Scanner f = new Scanner(file);
			String line = "";
			
			/*
			 * Read the sentence from file
			 */
			while (f.hasNextLine()) {
				line += f.nextLine();
			}

				String[] clauses = line.split("\\*");
				ArrayList<Clause> sentence = new ArrayList<Clause>();

				for (int i = 0; i < clauses.length; i++) {
					String[] literales = clauses[i].split("\\+");
					List<Literal> litInClause = new ArrayList<Literal>();
					int j = 0;
					for (j = 0; j < literales.length; j++) {
						/*
						 * Avoid parenthesis
						 */
						literales[j] = literales[j].replace("(", "").replace(")", "");
						/*
						 * Case ~X or X
						 */
						if (literales[j].contains("-")) {
							literales[j] = literales[j].replace("-", "");
							litInClause.add(new Literal(new PropositionSymbol(literales[j]), false));
						} else {
							litInClause.add(new Literal(new PropositionSymbol(literales[j]), true));
						}
					}
					sentence.add(new Clause(litInClause));
				}
				
				if(new Sentence(sentence).checkTwoSat()){
					
					System.out.println("Es 2-SAT");
					System.out.println(TwoSat.isSatisfiable(sentence));
					
				} else if (new Sentence(sentence).checkHornSat()){
					
					System.out.println("Es HORN-SAT");
					System.out.println(HornSat.isSatisfiable(sentence));
					
				} else {
					DPLLSat sat = new DPLLSat();
					System.out.println(sat.isSatisfiable(new Sentence(sentence)));
				}			
		} catch (Exception a) {
			a.printStackTrace();

		}

	}

}
