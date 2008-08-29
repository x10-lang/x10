/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.AbstractExtFactory_c;
import polyglot.ast.Ext;
import polyglot.ext.x10.extension.X10ClassBodyExt_c;
import polyglot.ext.x10.extension.X10Ext_c;

/**
 * ExtFactory for pao extension.
 */
public class X10ExtFactory_c extends AbstractExtFactory_c {
	public X10ExtFactory_c() {
		super();
	}

	public Ext extNodeImpl() {
		return new X10Ext_c();
	}

	protected Ext extClassBodyImpl() {
		return new X10ClassBodyExt_c();
	}

	public Ext extFutureImpl() {
		return extExprImpl();
	}

	public Ext extAsyncImpl() {
		return extStmtImpl();
	}

	public Ext extAtEachImpl() {
		return extStmtImpl();
	}

	public Ext extForEachImpl() {
		return extStmtImpl();
	}

	public Ext extForLoopImpl() {
		return extStmtImpl();
	}

	public Ext extFinishImpl() {
		return extStmtImpl();
	}

	public Ext extClosureImpl() {
		return extExprImpl();
	}
}
