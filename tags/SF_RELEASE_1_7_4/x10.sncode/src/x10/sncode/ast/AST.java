package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.InvalidClassFileException;

public class AST {
	public static enum Kind {
		CONSTANT, VAR, EQV, FORMULA, FIELDREF, METHODREF;
	
		public static Kind value(int k) {
			return values()[k];
		}
	}

	public static Term readTerm(ByteBuffer b) throws InvalidClassFileException {
		int k = b.getCPIndex();
		Kind kind = Kind.value(k);
		
		switch (kind) {
		case VAR:
		case EQV: {
			int index = b.getCPIndex();
			return new Var(kind, index);
		}
		case CONSTANT: {
			int index = b.getCPIndex();
			return new Constant(index);
		}
//		case FIELDREF: {
//			int index = b.getCPIndex();
//			return new FieldRef(index);
//		}
//		case METHODREF: {
//			int index = b.getCPIndex();
//			return new MethodRef(index);
//		}
		default: {
			int numTerms = b.getCount();
		    int name = b.getCPIndex();
			Term[] terms = new Term[numTerms];
			for (int i = 0; i < terms.length; i++) {
				Term t = readTerm(b);
				terms[i] = t;
			}
			return new Formula(name, terms);
		}
		}
	}
}
