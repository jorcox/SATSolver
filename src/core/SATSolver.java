package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ajeno.TwoSat;

import aima.core.logic.propositional.kb.data.Clause;
import aima.core.logic.propositional.kb.data.Literal;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;

/**
 * 
 * @author Javier Beltran Jorba, Jorge Cancer Gil
 *
 * Clase que contiene el programa principal.
 */
public class SATSolver {

	/**
	 * Obtiene la formula del fichero y comprueba si es
	 * satisfacible, informando por pantalla.
	 */
	public static void main(String[] args) {
		try {
			ArrayList<Clause> sentence;
			Scanner input;
			if (args.length > 0) {
				/* Si se pasa un fichero, se lee la formula que contiene */
				File ficheroSAT = new File(args[0]);
				input = new Scanner(ficheroSAT);				
			} else {
				/* Si no se pasa un fichero, la introduccion es manual */
				printMenu();
				input = new Scanner(System.in);
			}
			
			/*
			 * Lee la formula y la procesa para el programa
			 */
			String formula = readFormula(input);
			input.close();
			sentence = generateSentence(formula);
				
			/*
			 * Comprueba de que problema se trata y lo resuelve
			 */
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
		} catch (FileNotFoundException e) {
			System.out.println("Error: fichero no encontrado");
			System.exit(1);
		}
		

	}

	/**
	 * Dado un fichero que contiene una formula SAT, genera una sentencia
	 * equivalente en forma de lista de clausulas.
	 */
	private static ArrayList<Clause> generateSentence(String line) {
		/* Separa la formula en clausulas */
		String[] clauses = line.split("\\*");
		ArrayList<Clause> sentence = new ArrayList<Clause>();

		/* Procesa cada clausula de la formula */
		for (int i = 0; i < clauses.length; i++) {
			/* Separa la clausula en literales */
			String[] literales = clauses[i].split("\\+");		
			List<Literal> litInClause = new ArrayList<Literal>();
			
			/* 
			 * Procesa cada literal de la clausula, eliminando parentesis
			 * y procesando correctamente si son literales negados o no 
			 */
			for (int j = 0; j < literales.length; j++) {
				literales[j] = literales[j].replace("(", "").replace(")", "");
				if (literales[j].contains("-")) {
					literales[j] = literales[j].replace("-", "");
					litInClause.add(new Literal(new PropositionSymbol(literales[j]), false));
				} else {
					litInClause.add(new Literal(new PropositionSymbol(literales[j]), true));
				}
			}
			sentence.add(new Clause(litInClause));
		}
		
		return sentence;
	}
	
	/**
	 * Lee la formula de un canal de entrada hasta encontrar un finalizador, 
	 * que puede ser el final de fichero o una linea con "end"
	 */
	private static String readFormula(Scanner input) {
		String line = "";	
		boolean exit = false;
		while (!exit && input.hasNextLine()) {
			String part = input.nextLine();
			if (part.equals("end")) {
				/* Linea de fin de lectura */
				exit = true;
			} else {
				line += part;
			}
		}
		return line;
	}
	
	/**
	 * Muestra por pantalla el menu explicativo para que el usuario sepa
	 * como tiene que introducir la formula CNF
	 */
	private static void printMenu() {
		System.out.println("Este programa calcula si una formula CNF es satisfacible.");
		System.out.println("Puede introducir la formula CNF a continuacion.");
		System.out.println("La sintaxis es:");
		System.out.println("Disyuncion: +");
		System.out.println("Conjuncion: *");
		System.out.println("Negacion: -");
		System.out.println("Las variables deben empezar por letra, y pueden contener letras, numeros y _");
		System.out.println("Las clausulas aparecen separadas con ( )");
		System.out.println("La formula CNF puede ocupar varias lineas.");
		System.out.println("Para indicar que ha terminado su formula, escriba end en una linea nueva.");
		System.out.println();
		System.out.println("Un ejemplo de formula CNF seria:");
		System.out.println("(x_2 + -x_1 + x_0) * (x_0 + -c) * (x_2 + x_1)");
		System.out.println("* (x_1+c)");
		System.out.println("end");
		System.out.println();
		System.out.println("Introduzca su formula CNF: ");
	}

}
