/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types.constr;

import java.io.Serializable;

/**
 * A path is implemented as an untyped array of Strings --- each String is the name of the field.
 * @author vj
 *
 */
public class Path implements Serializable {

	String[] path;
	/**
	 * 
	 */
	public Path(String a) {
		this(new String[] { a});
	}
	public Path(String a, String b) {
		this(new String[]{a,b});
	}
	public Path(String a, String b, String c) {
		this(new String[]{a,b,c});
	}
	public Path(String a, String b, String c, String d) {
		this(new String[]{a,b,c,d});
	}
	public Path(String a, String b, String c, String d, String e) {
		this(new String[]{a,b,c,d,e});
	}
	public Path(String[] path) {
		this.path=path;
	}

}
