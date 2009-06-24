/**
 * 
 */
package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.ast.AST.Kind;

public class Constant extends Term {
	int constantIndex;

	public Constant(int constantIndex) {
		this.constantIndex = constantIndex;
	}

	@Override
	public Kind getKind() {
		return AST.Kind.CONSTANT;
	}
	
	public void write(ByteBuffer w) {
		w.addInt(AST.Kind.CONSTANT.ordinal());
		w.addInt(constantIndex);
	}

	public int getValue() {
		return constantIndex;
	}
}