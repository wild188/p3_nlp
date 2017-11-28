import java.util.ArrayList;

public class RHS {
		private ArrayList<String> rhs;
		private double prob;
		
		public RHS(ArrayList<String> _rhs, double _prob) {
			this.rhs = new ArrayList<String>();
			for (String s : _rhs) {
				this.rhs.add(s);
			}
			this.prob = _prob;
		}
		
		public double getProb() {
			return this.prob;
		}
		
		public void setProb(double _prob) {
			this.prob = _prob;
		}
		
		public String first() {
			return rhs.get(0);
		}
		
		public String second() {
			if (rhs.size() == 1) {
				return null;
			}
			return rhs.get(1);
		}
		
		public void printProduction(String lhs) {
			System.out.print(lhs + " ->");
			for (String s : rhs) {
				System.out.print(" " + s);
			}
			System.out.println();
		}

		public boolean contains(String test){
			if(!test.equals(rhs.get(0))){
				return false;
			}
			if(rhs.size() == 1){
				return false;
			}
			if(!test.equals(rhs.get(1))){
				return false;
			}
			return true;
		}

		@Override
		public String toString(){
			if(rhs.size() == 1){
				return rhs.get(0);
			}
			return rhs.get(0) + ", " + rhs.get(1);
		}
	}