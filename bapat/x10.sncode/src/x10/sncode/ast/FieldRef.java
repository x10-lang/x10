/**
 * 
 */
package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.ast.AST.Kind;

public class FieldRef extends Term {
	int fieldRefIndex;

	public FieldRef(int fieldRefIndex) {
		this.fieldRefIndex = fieldRefIndex;
	}

	@Override
	public Kind getKind() {
		return AST.Kind.FIELDREF;
	}
	
	public void write(ByteBuffer w) {
		w.addInt(AST.Kind.FIELDREF.ordinal());
		w.addInt(fieldRefIndex);
	}
	
	public int getFieldRef() {
		return fieldRefIndex;
	}
}