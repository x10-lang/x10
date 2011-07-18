package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.ast.AST.Kind;

/**
 * Formula modules binary and unary operations, calls, etc. It is up to the
 * annotation processor to interpret the name and to provide a semantics for the
 * operation.
 */
public class Formula extends Term {
	int nameIndex;
	Term[] children;

	public Formula(int name, Term[] children) {
		this.nameIndex = name;
		this.children = children;
	}

	@Override
	public Kind getKind() {
		return AST.Kind.FORMULA;
	}
	
	public void write(ByteBuffer w) {
		w.addInt(AST.Kind.FORMULA.ordinal());
		w.addInt(nameIndex);
		w.addInt(children.length);
		for (Term t : children) {
			t.write(w);
		}
	}
	
	public Term[] getArgs() {
		return children;
	}

	public int getName() {
		return nameIndex;
	}
}