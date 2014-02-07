/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util.typedump;

import polyglot.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.frontend.ExtensionInfo;

public class Main {
    public static void main(String args[]) {
	String extension = "jl";
	String className;
	if (args.length == 3 && args[0].equals("-ext"))
	    extension = args[1];
	if ((extension == null && args.length != 1) ||
	    (extension != null && args.length != 3)) {
	    System.err.println("Usage: " +
			       "polyglot.util.typedump.Main "+ 
			       "[-ext <extension>] <classname>");
	    System.exit(1);
	}

	if (extension == null)
	    className = args[0];
	else
	    className = args[2];

	ExtensionInfo extInfo = null;

        String extClassName =
            "polyglot.ext." + extension + ".ExtensionInfo";
        Class<?> extClass = null;

        try {
            extClass = Class.forName(extClassName);
        }
        catch (ClassNotFoundException e) {
            System.err.println( "Extension " + extension +
                                " not found: could not find class " +
                                extClassName + ".");
            System.exit( 1);
        }

        try {
            extInfo = (ExtensionInfo) extClass.newInstance();
        }
        catch (Exception e) {
            System.err.println( "Extension " + extension +
                                " could not be loaded: "+
                                "could not instantiate " + extClassName + ".");
            System.exit( 1);
        }

	try {
	    TypeSystem ts = extInfo.typeSystem();
	    TypeDumper t = TypeDumper.load(QName.make(className), ts);

	    CodeWriter cw = new OptimalCodeWriter(System.out, 72);
	    
	    t.dump(cw);
	    cw.newline(0);

	    try {
		cw.flush();
	    } catch (java.io.IOException exn) {
		System.err.println(exn.getMessage());
	    }
	} catch (java.io.IOException exn) {
	    System.err.println("IO errors.");
	    System.err.println(exn.getMessage());
	} catch (ClassNotFoundException exn) {
	    System.err.println("Could not load .class: "+className);
	    System.err.println(exn.getMessage());
	} catch (NoSuchFieldException exn) {
	    System.err.println("Could not reflect jlc fields");
	    System.err.println(exn.getMessage());
	} catch (SecurityException exn) {
	    System.err.println("Security policy error.");
	    System.err.println(exn.getMessage());
	}
    }
}
