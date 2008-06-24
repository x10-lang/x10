package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

public interface SubtypeTest extends Expr {

	public TypeNode supertype();

	public TypeNode subtype();

	public SubtypeTest supertype(TypeNode sup);

	public SubtypeTest subtype(TypeNode sub);

}
