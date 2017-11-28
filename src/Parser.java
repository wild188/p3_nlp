/**
 * Parser based on the CYK algorithm.
 */

import java.io.*;
import java.util.*;

class Options {
	public ArrayList<String> lhsList;

	public Options(){
		lhsList = new ArrayList<String>();
	}

	public Options(ArrayList<String> lhss){
		this();
		this.lhsList = lhss;
	}
}

public class Parser {

	public Grammar g;

	/**
	 * Constructor: read the grammar.
	 */
	public Parser(String grammar_filename) {
		g = new Grammar(grammar_filename);
	}

	// private Options lhsGenerator(Options b, Options c){
	// 	Options output = new Options();
	// 	for(int i = 0; i < b.lhsList.size(); i++){
	// 		ArrayList<String> blhsList = g.findLHS(b.lhsList.get(i));
	// 		for(int j = 0; j < c.lhsList.size(); j++){
	// 			ArrayList<String> clhsList = g.findLHS(c.lhsList.get(j));
	// 			for(int k = 0; k < clhsList.size(); k++){
	// 				int bindex = blhsList.indexOf(clhsList.get(k));
	// 				if(bindex >= 0){
	// 					int oindex = output.lhsList.indexOf(clhsList.get(k));
	// 					double probability = b.prob.get(bindex) * c.prob.get(k);
	// 					if(oindex < 0){
	// 						output.lhsList.add(clhsList.get(k));
	// 						output.prob.add(probability);
	// 					}else{
	// 						output.prob.add(oindex, (output.prob.get(oindex) + probability));
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}
	// 	return output;
	// }

	private Options[][] table;

	/**
	 * Parse one sentence given in the array.
	 */
	public void parse(ArrayList<String> sentence) {
		int len = sentence.size();
		for(String word : sentence){
			System.out.println(word);
		}

		//ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>(len + 1);
		table = new Options[len + 1][len + 1];
		//back = new int[len+1][len+1][30];

		for(int j = 1; j < len + 1; j++){
			String word = sentence.get(j - 1);
			table[j - 1][j] = new Options(g.findPreTerminals(word));
			// String word = sentence.get(j - 1);
			// ArrayList<String> lhsList = g.findLHS(word);
			// table[j - 1][j] = new Options();
			// for(String lhs : lhsList){
			// 	ArrayList<RHS> rhsList = g.findProductions(lhs);
			// 	for(RHS possible : rhsList){
			// 		if(possible.contains(word)){
			// 			int index = table[j - 1][j].lhsList.indexOf(lhs);
			// 			if(index < 0){
			// 				table[j - 1][j].lhsList.add(lhs);
			// 				table[j - 1][j].prob.add(possible.getProb());
			// 			}else{
			// 				table[j - 1][j].prob.add(index, table[j - 1][j].prob.get(index) + possible.getProb());
			// 			}
			// 		}
			// 	}
			// }

			for(int i = j - 2; i > 0; i--){
				for(int k = i + 1; k < j - 1; k++){
					// if(table[i][k] == null || table[k][j] == null){
					// 	continue;
					// }
					// Options b = table[i][k];
					// Options c = table[k][j];
					// if(table[i][j] == null){
					// 	table[i][j] = lhsGenerator(b, c);
					// }else{
					// 	Options newOptions = lhsGenerator(b, c);
					// 	table[i][j] = mergeOptions(newOptions, table[i][j]);
					// }
				}
			}
		}
	}
	/**
	 * Print the parse obtained after calling parse()
	 */
	public String PrintOneParse() {
		return null;
	}
	
	public static void main(String[] args) {
		// read the grammar in the file args[0]
		Parser parser = new Parser(args[0]);

		ArrayList<String> sentence = new ArrayList<String>(); // otherwise undefined BILLY
		String end = "$.$"; //otherwise undefined BILLY

		// read a parse tree from a bash pipe
		try {
			InputStreamReader isReader = new InputStreamReader(System.in);
			BufferedReader bufReader = new BufferedReader(isReader);
			while(true) {
				String line = null;
				if((line=bufReader.readLine()) != null) {
					
					String []words = line.split(" ");
					for (String word : words) {
						word = word.replaceAll("[^a-zA-Z.!]", "");
						if (word.length() == 0) {
							continue;
						}
						// use the grammar to filter out non-terminals and pre-terminals
						if (parser.g.symbolType(word) == 0) {
							if(!word.equals(".") && !word.equals("!")){
								sentence.add(word);
							}else if(word.contains(".")){
								end = ".";
							}else if(word.contains("!")){
								end = "!";
							}
						}
					}
				}
				else {
					break;
				}
			}
			bufReader.close();
			isReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		parser.parse(sentence);

		
		System.out.println("(ROOT " + parser.PrintOneParse() + " " + end + ")");
	}
}
