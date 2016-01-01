package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ajeno.Clause;
import ajeno.Literal;
import ajeno.TwoSat;
import ajeno.TwoSatR;

public class TwoSatS {
	public static void main(String[] args) {
		/*
		 * Lectura del fichero
		 */
		String ficheroSAT = args[0];
		File file = new File(ficheroSAT);
		try {
			Scanner fichero = new Scanner(file);
			String line = "";
			while (fichero.hasNextLine()) {

				line = fichero.nextLine();
				String[] ors = line.split("\\*");
				ArrayList<Clause<String>> orClauses = new ArrayList<Clause<String>>();

				for (int i = 0; i < ors.length; i++) {
					String[] literales = ors[i].split("\\+");
					List<Literal<String>> litInClause = new ArrayList<Literal<String>>();
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
							litInClause.add(new Literal<String>(literales[j], false));
						} else {
							litInClause.add(new Literal<String>(literales[j], true));
						}
					}
					if (j == 1) {
						orClauses.add(new Clause<String>(litInClause.get(0), litInClause.get(0)));
					} else {
						orClauses.add(new Clause<String>(litInClause.get(0), litInClause.get(1)));
					}

				}
				boolean sat = TwoSatR.isSatisfiable(orClauses);
				System.out.println(sat);
			}
		} catch (Exception a) {
			a.printStackTrace();

		}

	}
}
