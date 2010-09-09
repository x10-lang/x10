/**
 * 
 */
package x10.sncode.ast;

import x10.sncode.ByteBuffer;
import x10.sncode.ast.AST.Kind;

public abstract class Term {
	public abstract void write(ByteBuffer w);
	public abstract Kind getKind();
}