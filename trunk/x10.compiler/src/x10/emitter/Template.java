/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.emitter;

import polyglot.util.InternalCompilerError;
import polyglot.visit.Translator;
import x10.visit.X10PrettyPrinterVisitor;

/**
 * Expand a given template with the given set of arguments.
 * Equivalent to a Loop with an array of singleton lists.
 * If the template has zero, one, two, or three arguments, the
 * arguments can be passed in directly to the constructor.
 */
public class Template extends Expander {
	/**
	 * 
	 */
	
	private final String id;
	//private final String template;
	private final Object[] args;
	public Template(Emitter er, String id, Object... args) {
		super(er);
		
		this.id = id;
		this.args = args;
	}
	public void expand() {
		expand(er.tr);
	}
	public void expand(Translator tr) {
		er.dump(id, args, tr);
	}
	public String toString() {
		return id + " " + er.convertToString(args);
	}
	
}