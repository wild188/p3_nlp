/**
 * Generate sentences from a CFG
 * 
 * @author sihong
 *
 */

import java.io.*;
import java.util.*;

public class Generator {
	
	private final int TERMIAL = 0;
	private final int PRETERMINAL = 1;
	private final int NONTERMINAL = 2;
	private final String ROOT = "ROOT";


	private Grammar grammar;

	/**
	 * Constructor: read the grammar.
	 */
	public Generator(String grammar_filename) {
		grammar = new Grammar(grammar_filename);
	}

	private RHS selectRHS(ArrayList<RHS> possibilities){
		int len = possibilities.size();
		if(possibilities == null || len < 1){
			//Error
			return null;
		}
		double[] mapping = new double[len];
		double curmax = 0;
		for(int i = 0; i < len; i++){
			curmax += possibilities.get(i).getProb();
			mapping[i] = curmax;
			//System.out.println(i + " : " + possibilities.get(i).toString() + " = " + curmax);
		}

		double rand = Math.random() * curmax;
		//System.out.println(rand);
		for(int i = 0; i < len; i++){
			if(rand < mapping[i]){
				return possibilities.get(i);
			}
		}
		return possibilities.get(len);
	}

	/**
	 * Recursively generates until it hits a terminal
	 */
	private String recursiveGen(String lhs){
		//Base case
		if(grammar.symbolType(lhs) == TERMIAL){
			return (" " + lhs); 
		}

		//Recursive case
		ArrayList<RHS> possibilities = grammar.findProductions(lhs);
		RHS rhs = selectRHS(possibilities);
		String first = rhs.first();
		StringBuilder output = new StringBuilder();
		output.append("(");
		output.append(lhs);
		output.append(recursiveGen(first));
		String second = rhs.second();
		if(second != null){
			output.append(recursiveGen(second));
		}
		output.append(") ");
		return output.toString();
	}

	/**
	 * Generate a number of sentences.
	 */
	public ArrayList<String> generate(int numSentences) {
		if(numSentences < 1){
			grammar.printGrammarInfo();
			return null;
		}
		
		ArrayList<String> sentences = new ArrayList<String>(numSentences);
		for(int i = 0; i < numSentences; i++){
			sentences.add(i, recursiveGen(ROOT));
		}

		return sentences;
	}
	
	public static void main(String[] args) {
		// the first argument is the path to the grammar file.
		Generator g = new Generator(args[0]);
		ArrayList<String> res = g.generate(1);
		for (String s : res) {
			System.out.println(s);
		}
	}
}
