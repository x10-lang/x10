package x10.sncode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constraint {
	List<Term> terms;

	public static final int CONSTANT = 0;
	public static final int VAR = 1;
	public static final int EQV = 2;
	public static final int FIELD = 3;
	public static final int FORMULA = 4;

	public static class Field extends Term {
		Term target;
		int fieldRefIndex;

		public Field(Term target, int fieldRefIndex) {
			this.target = target;
			this.fieldRefIndex = fieldRefIndex;
		}

		@Override
		public int getKind() {
			return FIELD;
		}
		
		public void write(ByteBuffer w) {
			w.addInt(FIELD);
			w.addInt(fieldRefIndex);
			target.write(w);
		}

		public Term getTarget() {
			return target;
		}
		
		public int getName() {
			return fieldRefIndex;
		}
	}

	public static class Formula extends Term {
		int op;
		Term[] children;

		public Formula(int op, Term[] children) {
			this.op = op;
			this.children = children;
		}

		@Override
		public int getKind() {
			return FORMULA;
		}
		
		public void write(ByteBuffer w) {
			w.addInt(FORMULA);
			w.addInt(op);
			w.addInt(children.length);
			for (Term t : children) {
				t.write(w);
			}
		}
		
		public Term[] getArgs() {
			return children;
		}

		public int getName() {
			return op;
		}
	}

	public static abstract class Term {
		public abstract void write(ByteBuffer w);

		public abstract int getKind();
	}

	public static class Constant extends Term {
		int constantIndex;

		public Constant(int constantIndex) {
			this.constantIndex = constantIndex;
		}

		@Override
		public int getKind() {
			return CONSTANT;
		}
		
		public void write(ByteBuffer w) {
			w.addInt(CONSTANT);
			w.addInt(constantIndex);
		}

		public int getValue() {
			return constantIndex;
		}
	}

	public static class Var extends Term {
		int op;
		int nameIndex;

		public Var(int op, int nameIndex) {
			this.op = op;
			this.nameIndex = nameIndex;
		}

		@Override
		public int getKind() {
			return op;
		}
		
		public void write(ByteBuffer w) {
			w.addInt(op);
			w.addInt(nameIndex);
		}

		public int getName() {
			return nameIndex;
		}
	}
	
	public Constraint(Term... t) {
		terms = Arrays.asList(t);
	}

	public Constraint(ByteBuffer b, int offset, int len)
			throws InvalidClassFileException {
		b.seek(offset);
		int numTerms = b.getCount();
		terms = new ArrayList<Term>(numTerms);
		for (int i = 0; i < numTerms; i++) {
			Term t = readTerm(b);
			terms.add(t);
		}
		if (b.offset() != offset + len)
			throw new InvalidClassFileException(offset, "bad constraint");
	}

	public void write(ByteBuffer w) {
		int offset = w.offset();
		w.addLength(0);
		w.addCount(terms.size());
		for (Term t : terms) {
			t.write(w);
		}
		w.setLength(offset, w.offset() - offset - 4);
	}

	private Term readTerm(ByteBuffer b) throws InvalidClassFileException {
		int op = b.getCPIndex();

		switch (op) {
		case VAR:
		case EQV: {
			int index = b.getCPIndex();
			return new Var(op, index);
		}
		case FIELD: {
			int index = b.getCPIndex();
			Term t = readTerm(b);
			return new Field(t, index);
		}
		case CONSTANT: {
			int index = b.getCPIndex();
			return new Constant(index);
		}
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

	public List<Term> getTerms() {
		return terms;
	}

	public void addTerm(Term t) {
		terms.add(t);
	}
}
