/**
 * 
 */
package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.ast.AST.Kind;

public class Var extends Term {
	Kind kind;
	int nameIndex;

	public Var(Kind kind, int nameIndex) {
		this.kind = kind;
		this.nameIndex = nameIndex;
	}

	@Override
	public Kind getKind() {
		return kind;
	}
	
	public void write(ByteBuffer w) {
		w.addInt(kind.ordinal());
		w.addInt(nameIndex);
	}

	public int getName() {
		return nameIndex;
	}
}