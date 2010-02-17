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

package x10.types;

import polyglot.types.Flags;
import polyglot.types.PrimitiveType;
import polyglot.types.PrimitiveType_c;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;

/** X10 has no primitive types. Types such as int etc are all value class types. 
 * However, this particular X10 implementation uses Java primitive types to implement some of
 * X10's value class types, namely, char, boolean, byte, int etc etc. It implements other
 * value class types as Java classes.
 * 
 * Thus this class represents one of specially implemented X10 value class types.
 * @author praun
 * @author vj
 */
public class X10PrimitiveType_c extends PrimitiveType_c implements X10PrimitiveType {
	protected X10PrimitiveType_c() { }

	public X10PrimitiveType_c(TypeSystem ts, Name name) {
		super(ts, name);
	}

	public void print(CodeWriter w) {
		w.write(name().toString());
	}

	private static String getStackTrace() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = new Throwable().getStackTrace();
		for (int i=2; i < trace.length; i++)
			sb.append("\t").append(trace[i]).append("\n");
		return sb.toString();
	}

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    if (flags != null && ! flags.equals(X10Flags.NONE)) {
	    	sb.append(flags.toString()).append(" ");
	    }
	    sb.append(super.toString());
	    return sb.toString();
	}
	
	public String typeName() { 
	    return super.toString();
	}
	
	public boolean isX10Struct() { return true;}
	/* All primitive types are structs. */

	Flags flags = X10Flags.NONE;
	public Flags flags() {
		return flags;
	}
	// No flags can be added to primitives. They are struct and not rooted.
	public Type setFlags(Flags flags) {
		return this;
	}
	public Type clearFlags(Flags flags) {
		X10PrimitiveType_c c = (X10PrimitiveType_c) copy();
		if (c.flags != null) {
			c.flags = c.flags.clear(flags);
		}
		return c;
	}
	 public boolean equalsNoFlag(Type t2) {
			return this == t2;
		}
}
