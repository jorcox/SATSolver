package core;

import java.util.Random;

/**
 * Clase encargada de crear formulas CNF aleatorias con los parametros
 * indicados.
 */
public class Test {
	
	private static final int LITS_2SAT = 2;

	/**
	 * Devuelve un String con una formula CNF del tipo indicado, con
	 * un maximo de <lit> literales distintos y con exactamente <claus>
	 * clausulas, o null si se introduce un tipo indefinido.
	 */
	public static String generate(String type, int lit, int claus) {
		if (type.equals("2-sat")) {
			return generate2Sat(lit, claus);
		} else if (type.equals("horn-sat")) {
			return generateHornSat(lit, claus);
		} else if (type.equals("dpll") || type.equals("walksat")) {
			return generateSAT(lit, claus);
		} else {
			return null;
		}
	}

	/**
	 * Genera una formula 2-SAT.
	 */
	private static String generate2Sat(int lit, int claus) {
		String formula = "";
		/* Genera cada clausula */
		for (int i=0; i<claus; i++) {
			formula += "(";
			/* Incluye entre 1 y 2 literales aleatorios */
			for (int j=0; j<LITS_2SAT; j++) {
				formula += getRandomLiteral(lit) + "+";
			}
			formula = formula.substring(0, formula.length()-1);
			formula +=")*";
		}
		return formula.substring(0, formula.length()-1);
	}
	
	/**
	 * Genera una formula de Horn.
	 */
	private static String generateHornSat(int lit, int claus) {
		String formula = "";
		int max = 0;
		/* Genera cada clausula */
		for (int i=0; i<claus; i++) {
			/* 
			 * Asegura que alguna clausula tiene 3 o mas literales,
			 * para que no sea 2-SAT.
			 */
			int maxLits = new Random().nextInt(lit) +1;
			if (maxLits > max) {
				max = maxLits;
			}
			if (i==claus-1 && max<3) {
				while (maxLits< 3) {
					maxLits = new Random().nextInt(lit) +1; 		
				}
			}
			
			String clausula = "(";
			for (int j=0; j<maxLits; j++) {
				/* Genera un primer literal positivo y el resto negados */
				if (j==0) {
					clausula += getPositiveLiteral(lit) + "+";
				} else {
					String nuevo = getNegativeLiteral(lit) + "+";
					while (clausula.contains(nuevo)) {
						/* Asegura que no introduce un literal repetido */
						nuevo = getNegativeLiteral(lit) + "+";
					}
					clausula += nuevo;
				}
			}
			clausula = clausula.substring(0, clausula.length()-1);
			clausula +=")*";
			formula += clausula;
		}
		return formula.substring(0, formula.length()-1);
	}

	/**
	 * Genera una formula SAT que no es ni 2-SAT ni Horn-SAT.
	 */
	private static String generateSAT(int lit, int claus) {
		String formula = "";
		int max = 0;
		/* Genera cada clausula */
		for (int i=0; i<claus; i++) {
			/* 
			 * Asegura que alguna clausula tiene 3 o mas literales,
			 * para que no sea 2-SAT.
			 */
			int maxLits = new Random().nextInt(lit) +1;
			if (maxLits > max) {
				max = maxLits;
			}
			if (i==claus-1 && max<3) {
				while (maxLits< 3) {
					maxLits = new Random().nextInt(lit) +1; 		
				}
			}
			
			String clausula = "(";
			for (int j=0; j<maxLits; j++) {
				String nuevo = getRandomLiteral(lit);
				while (clausula.contains(nuevo)) {
					/* Asegura que no introduce un literal repetido */
					nuevo = getRandomLiteral(lit);
				}
				clausula += nuevo + "+";
			}
			clausula = clausula.substring(0, clausula.length()-1);
			clausula +=")*";
			formula += clausula;
		}
		return formula.substring(0, formula.length()-1);
	}
	
	/**
	 * Devuelve un literal de la forma a_X, donde X es
	 * un numero aleatorio entre 0 y lit-1. El literal puede
	 * ir iniciado por - o no.
	 */
	private static String getRandomLiteral(int lit) {
		if (new Random().nextBoolean()) {
			return "a_" + new Random().nextInt(lit);
		} else {
			return "-a_" + new Random().nextInt(lit);
		}
	}
	
	/**
	 * Devuelve un literal de la forma a_X, donde X es
	 * un numero aleatorio entre 0 y lit-1.
	 */
	private static String getPositiveLiteral(int lit) {
		return "a_" + new Random().nextInt(lit);
	}
	
	/**
	 * Devuelve un literal de la forma -a_X, donde X es
	 * un numero aleatorio entre 0 y lit-1.
	 */
	private static String getNegativeLiteral(int lit) {
		return "-a_" + new Random().nextInt(lit);
	}
	
}
