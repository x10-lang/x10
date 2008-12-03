package dpll;

import java.io.StreamTokenizer;


public class Problem {
	public static enum TruthValue {
		Unknown,
		True,
		False
	}
	public static class Variable {
		public final int index;
		public  TruthValue value = TruthValue.Unknown; 
		public Variable(int n) {index=n;}
		public String toString() { return "v(" + index+", "+ value +")";}

	}
	public  class Clause {
		Variable[] pos;
		Variable[] neg;
		// the same variable cannot occur in pos and neg.
		public Clause(int[] lits) {
			int n = lits.length;
			assert n > 0;
			int nPos = 0;
			for (int lit : lits ) {
				assert lit != 0;
				if (lit > 0) ++nPos;
			}
			pos = new Variable[nPos];
			neg = new Variable[n-nPos];
			int iPos=0, iNeg=0;
			for (int lit : lits ) {
				if (lit > 0) 
					pos[iPos++] = variables[lit]; 
				else
					neg[iNeg++] = variables[-lit];
			}
			
		}
	}
	Variable[] variables;
	Clause[] clauses;
	
	public Problem(StreamTokenizer st) {
		
	}
	
	public static  Problem readProblem(String fn) {
		return null;
	}

}
