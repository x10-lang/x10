/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Ext;
import polyglot.ast.AbstractExtFactory_c;
import polyglot.ext.x10.extension.X10BinaryExt_c;
import polyglot.ext.x10.extension.X10CanonicalTypeNodeExt_c;
import polyglot.ext.x10.extension.X10CastExt_c;
import polyglot.ext.x10.extension.X10ClassBodyExt_c;
import polyglot.ext.x10.extension.X10Ext_c;
import polyglot.ext.x10.extension.X10FutureExt_c;
import polyglot.ext.x10.extension.X10InstanceofExt_c;
import polyglot.ext.x10.extension.X10NullableNodeExt_c;

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

	public Ext extInstanceofImpl() {
		return new X10InstanceofExt_c();
	}

	protected Ext extClassBodyImpl() {
		return new X10ClassBodyExt_c();
	}

	public Ext extCastImpl() {
		return new X10CastExt_c();
	}

	public Ext extBinaryImpl() {
		return new X10BinaryExt_c();
	}

	public Ext extFutureImpl() {
		return new X10FutureExt_c();
	}

	public Ext extCanonicalTypeNodeImpl() {
		return new X10CanonicalTypeNodeExt_c();
	}

	public Ext extNullableNodeImpl() {
		return new X10NullableNodeExt_c();
	}

	public Ext extFutureNodeImpl() {
		return extNodeImpl();
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
