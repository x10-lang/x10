/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.types.PrimitiveType;
import polyglot.types.PrimitiveType_c;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;

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
	    sb.append(super.toString());
	    return sb.toString();
	}
	
	public String typeName() { 
	    return super.toString();
	}
	
	public boolean isValueType() { return ((X10TypeSystem) typeSystem()).isValueType(this); }
	
	/** All primitive types are safe. */
	public boolean safe() { return true; }
}
