package polyglot.ext.x10.ast;

import polyglot.ast.Expr;

public interface Contains extends Expr {
	Expr item();
	Contains item(Expr sub);

	Expr collection();
	Contains collection(Expr sup);

	Contains isSubsetTest(boolean f);
	boolean isSubsetTest();

}
