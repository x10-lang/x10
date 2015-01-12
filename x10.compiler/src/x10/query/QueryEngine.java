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

package x10.query;

import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.Unary;
import polyglot.types.JavaArrayType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ast.SettableAssign;
import x10.types.X10ClassType;
import polyglot.types.Context;
import polyglot.types.TypeSystem;

/**
 * A query engine for analysis results and other properties of various program
 * elements.
 *
 * @author Igor Peshansky
 */
public class QueryEngine {
	private static QueryEngine instance_;
	public static void init(ExtensionInfo ext) {
		instance_ = new QueryEngine(ext);
	}
	public static QueryEngine INSTANCE() {
		return instance_;
	}

	private ExtensionInfo ext;

	private QueryEngine(ExtensionInfo ext) {
		this.ext = ext;
	}
}

