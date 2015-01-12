/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import polyglot.ast.AbstractExtFactory_c;
import polyglot.ast.Ext;
import x10.extension.X10Ext_c;

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
