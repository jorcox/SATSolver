package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import algorithms.*;
import utils.*;

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
			Scanner input = null;
			String type = "dpll";
			boolean test = false;
			String typeTest = "2-SAT";
			int lit = 0;
			int claus = 0;
			
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-file")) {
					/* Si se pasa un fichero, se lee la formula que contiene */
					File ficheroSAT = new File(args[i+1]);
					printSentence(ficheroSAT);
					input = new Scanner(ficheroSAT);
				} else if (args[i].equals("-type")) {
					/* Se puede especificar el tipo de algoritmo (dpll o walksat) */
					type = args[i+1];
				} else if (args[i].equals("-test")) {
					/* Modo en el que se ejecuta una prueba aleatoria */
					test = true;
					typeTest = args[i+1];
					if (typeTest.equals("dpll") || typeTest.equals("walksat")) {
						type = typeTest;
					}
					lit = Integer.parseInt(args[i+2]);
					claus = Integer.parseInt(args[i+3]);
				}
			}
			
			if (!test && input==null) {
				/* Si no se pasa un fichero, la introduccion es manual */
				printMenu();
				input = new Scanner(System.in);
			}
			
			if (test) {
				/* Genera una sentencia aleatoria de prueba */
				String formula = Test.generate(typeTest, lit, claus);
				sentence = generateSentence(formula);
				if (formula == null || sentence == null) {
					System.out.println("Error: test indicado incorrectamente.");
					System.exit(5);
				}
				System.out.println("Generada formula aleatoria de " + claus + " clausulas " +
						"y hasta " + lit + " literales distintos.");
			} else {
				/*
				 * Lee la formula y la procesa para el programa
				 */
				String formula = readFormula(input);
				input.close();
				sentence = generateSentence(formula);
				if (sentence == null) {
					System.out.println("Error: literal introducido incorrectamente.");
					System.exit(4);
				}
			}	
				
			/*
			 * Comprueba de que problema se trata y lo resuelve
			 */
			if(new Sentence(sentence).checkTwoSat()){			
				System.out.println("Es 2-SAT");
				long t1 = System.currentTimeMillis();
				boolean result = TwoSat.isSatisfiable(sentence);
				long t2 = System.currentTimeMillis();
				printResult(result);
				System.out.println("Tiempo empleado: " + (t2-t1) + " ms");
			} else if (new Sentence(sentence).checkHornSat()){			
				System.out.println("Es HORN-SAT");
				long t1 = System.currentTimeMillis();
				boolean result = HornSat.isSatisfiable(sentence);
				long t2 = System.currentTimeMillis();
				printResult(result);
				System.out.println("Tiempo empleado: " + (t2-t1) + " ms");
			} else {
				System.out.println("No es 2-SAT ni HORN-SAT");
				
				/*
				 * Lee el fichero de propiedades
				 */
				Properties props = new Properties();
				InputStream file = new FileInputStream("application.properties");
				props.load(file);
				
				/*
				 * Elige el algoritmo segun lo indicado por parametro
				 */
				if (type.equals("dpll")) {
					System.out.println("Utilizando algoritmo DPLL");
					
					DPLLSat sat = new DPLLSat();
					long t1 = System.currentTimeMillis();
					boolean result = sat.isSatisfiable(new Sentence(sentence));
					long t2 = System.currentTimeMillis();
					printResult(result);
					System.out.println("Tiempo empleado: " + (t2-t1) + " ms");
				} else if (type.equals("walksat")) {
					double probRandom = Double.parseDouble(props.getProperty("walksat.probrandom"));
					int maxSteps = Integer.parseInt(props.getProperty("walksat.maxsteps"));
					System.out.println("Utilizando WalkSAT con p=" + probRandom + " y maxSteps=" + maxSteps);
					
					WalkSAT sat = new WalkSAT(probRandom, maxSteps);
					long t1 = System.currentTimeMillis();
					boolean result = sat.isSatisfiable(new Sentence(sentence));
					long t2 = System.currentTimeMillis();
					printResult(result);
					System.out.println("Tiempo empleado: " + (t2-t1) + " ms");
				} else {
					System.out.println("Error: algoritmo introducido no conocido");
				}
			}	
		} catch (FileNotFoundException e) {
			System.out.println("Error: fichero no encontrado");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error: fichero de propiedades incorrecto");
			System.exit(2);
		} catch (NumberFormatException e) {
			System.out.println("Error: valores incorrectos en fichero de propiedades");
			System.exit(3);
		} catch (Exception e) {
			System.out.println("Error: desconocido");
			System.exit(6);
		}
	}

	/**
	 * Muestra por pantalla si es satisfacible o no la formula introducida.
	 */
	private static void printResult(boolean result) {
		if (result) {
			System.out.println("Es satisfacible");
		} else {
			System.out.println("No es satisfacible");
		}
	}

	/**
	 * Dado un fichero que contiene una formula SAT, genera una sentencia
	 * equivalente en forma de lista de clausulas.
	 */
	private static ArrayList<Clause> generateSentence(String line) {
		/* Separa la formula en clausulas */
		line = line.replace(" ", "");
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
					if (!literales[j].substring(0,1).matches("[A-Za-z]") ||
							includes(literales[j], "[^A-Za-z0-9_]")) {
						/* Comprueba que las variables tengan la sintaxis correcta */
						return null;
					}
					litInClause.add(new Literal(new PropositionSymbol(literales[j]), false));
				} else {
					if (!literales[j].substring(0,1).matches("[A-Za-z]") ||
							includes(literales[j], "[^A-Za-z0-9_]")) {
						/* Comprueba que las variables tengan la sintaxis correcta */
						return null;
					}
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
		System.out.println("Las clausulas pueden aparecer separadas con ( )");
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
	
	/**
	 * Lee una formula y la presenta por pantalla
	 */
	private static void printSentence(File ficheroSAT) throws IOException {
		System.out.println("Formula introducida:");
		BufferedReader reader = new BufferedReader(new FileReader(ficheroSAT));
		
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			line = reader.readLine();
		}
		reader.close();
	}
	
	/**
	 * Comprueba si un literal incluye algun caracter que aparece en regex
	 */
	private static boolean includes(String lit, String regex) {
		String aux = lit.replaceAll(regex, "");
		return lit.length() != aux.length();
	}

}
