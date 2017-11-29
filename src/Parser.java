/**
 * Parser based on the CYK algorithm.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Options hols the possible list of lhs that a particular cell could have in a CKY table
 */
class Options {
	public ArrayList<String> lhsList;

	public Options(){
		lhsList = new ArrayList<String>();
	}

	public Options(ArrayList<String> lhss){
		this();
		this.lhsList = lhss;
	}

	@Override
	public String toString(){
		StringBuilder out = new StringBuilder();
		for(String s : lhsList){
			out.append(s);
			out.append(", ");
		}
		return out.toString();
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

	/**
	 * Generates an options object with a list of possible left hand side
	 * given two options objects representing the two parts of the right hand side
	 */
	private Options lhsGenerator(Options b, Options c){
		Options output = new Options();
		boolean empty = true;
		for(String rhs1 : b.lhsList){
			for(String rhs2 : c.lhsList){
				ArrayList<String> lhsList = g.findLHS(rhs1 + " " + rhs2);
				if(lhsList != null){
					for(String lhs : lhsList){
						empty = false;
						if(!output.lhsList.contains(lhs)){
							output.lhsList.add(lhs);
						}
					}
				}
			}
		}
		return output;
	}

	/**
	 * Takes two option objects and return one with a combination of their two lhsLists
	 * removes any duplicates
	 */
	private Options mergeList(Options a, Options b){
		Options out = new Options(a.lhsList);
		for(String lhs : b.lhsList){
			if(!out.lhsList.contains(lhs)){
				out.lhsList.add(lhs);
			}
		}
		return out;
	}

	//Used in the CKY algorithm
	private Options[][] table;

	/**
	 * Parse one sentence given in the array.
	 */
	public void parse(ArrayList<String> sentence) {
		int len = sentence.size();
		table = new Options[len + 1][len + 1];

		for(int j = 1; j <= len; j++){
			String word = sentence.get(j - 1);
			table[j - 1][j] = new Options(g.findPreTerminals(word));

			for(int i = j - 2; i >= 0; i--){
				for(int k = i + 1; k <= j - 1; k++){
					if(table[i][k] == null || table[k][j] == null){
						continue;
					}
					Options b = table[i][k];
					Options c = table[k][j];
					if(table[i][j] == null){
						table[i][j] = lhsGenerator(b, c);
					}else{
						Options toAdd = lhsGenerator(b, c);
						table[i][j] = mergeList(table[i][j], toAdd);
					}
				}
			}
		}
	}

	/**
	 * A recursive function for finding a single possible parse tree from
	 * the table array created by the parse SKY method
	 */
	private String drillDown(int x, int y, int lhsIndex, ArrayList<String> sentence){
		StringBuilder output = new StringBuilder();
		Options target = table[x][y];
		String lhs = target.lhsList.get(lhsIndex);
		output.append("(");
		if(x == y -1 && x < sentence.size()){
			//base case
			output.append(lhs);
			output.append(" ");
			output.append(sentence.get(x));
		}else{
			//recursive case
			for (int k = x + 1; k <= y - 1; k++) {
				if (table[x][k] == null || table[k][y] == null) {
					continue;
				}
				int lhs1Index = 0;
				for(String rhs1 : table[x][k].lhsList){
					int lhs2Index = 0;
					for(String rhs2 : table[k][y].lhsList){
						ArrayList<String> possibleLHSlist = g.findLHS(rhs1 + " " + rhs2);
						if(possibleLHSlist != null){
							for(String possibleLHS : possibleLHSlist){
								if(possibleLHS != null && possibleLHS.equals(lhs)){
									output.append(lhs);
									output.append(" ");
									output.append(drillDown(x, k, lhs1Index, sentence));
									output.append(drillDown(k, y, lhs2Index, sentence));
								}
							}
						}
						lhs2Index++;
					}
					lhs1Index++;
				}
			}
		}
		output.append(") ");
		return output.toString();
	}

	/**
	 * Print the parse obtained after calling parse()
	 */
	public String PrintOneParse(ArrayList<String> sentence) {
		Options rootNode = table[0][table.length - 1 ];
		int sIndex = rootNode.lhsList.indexOf("S");
		if(sIndex < 0){
			return "No parse tree found.";
		}
		String parseString = drillDown(0, table.length - 1, sIndex, sentence);
		return parseString;
	}
	
	public static void main(String[] args) {
		// read the grammar in the file args[0]
		Parser parser = new Parser(args[0]);
		ArrayList<ArrayList<String>> toParse = new ArrayList<ArrayList<String>>();

		// read a parse tree from a bash pipe
		try {
			InputStreamReader isReader = new InputStreamReader(System.in);
			BufferedReader bufReader = new BufferedReader(isReader);
			ArrayList<String> sentence = new ArrayList<String>(); // otherwise undefined BILLY
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
							}else if(word.contains(".")){ //Marks the end of a sentence
								sentence.add(word);
								//end = ".";
								toParse.add(sentence);
								sentence = new ArrayList<String>();
							}else if(word.contains("!")){	//Marks the end of a sentence
								sentence.add(word);
								toParse.add(sentence);
								sentence = new ArrayList<String>();
								//end = "!";
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

		//Loops through and parses each sentence read in
		for(ArrayList<String> sentence : toParse){
			String end = sentence.get(sentence.size() - 1);
			sentence.remove(sentence.size() - 1);
			parser.parse(sentence);
			System.out.println("(ROOT " + parser.PrintOneParse(sentence) + " " + end + ")");
		}		
	}
}
