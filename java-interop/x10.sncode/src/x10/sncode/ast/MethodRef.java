/**
 * 
 */
package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.ast.AST.Kind;

public class MethodRef extends Term {
	int methodRefIndex;

	public MethodRef(int methodRefIndex) {
		this.methodRefIndex = methodRefIndex;
	}

	@Override
	public Kind getKind() {
		return AST.Kind.METHODREF;
	}
	
	public void write(ByteBuffer w) {
		w.addInt(AST.Kind.METHODREF.ordinal());
		w.addInt(methodRefIndex);
	}
	
	public int getFieldRef() {
		return methodRefIndex;
	}
}