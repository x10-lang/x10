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
import polyglot.types.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import x10.util.CollectionFactory;

class TypeDumper {
    static Set<Class<?>> dontExpand;
    static {
	Class<?>[] primitiveLike = {
	    Void.class,
	    Boolean.class,
	    Short.class,
	    Integer.class,
	    Long.class,
	    Float.class,
	    Double.class,
	    Class.class,
	    String.class,
	};
	dontExpand =
	    CollectionFactory.newHashSet(java.util.Arrays.asList(primitiveLike));
    }

    TypeObject theType;
    QName rawName;
    String compilerVersion;
    Date timestamp;
    TypeDumper(QName rawName, TypeObject t, String compilerVersion,
	       Long timestamp) {
	theType = t;
	this.rawName = rawName;
	this.compilerVersion = compilerVersion;
	this.timestamp = new Date(timestamp.longValue());
    }
    
    public static TypeDumper load(QName name, TypeSystem ts)
	throws ClassNotFoundException, NoSuchFieldException, 
	       java.io.IOException, SecurityException
    {
	Class<?> c = Class.forName(name.toString());
	try {
	    Field jlcVersion = c.getDeclaredField("jlc$CompilerVersion");
	    Field jlcTimestamp = c.getDeclaredField("jlc$SourceLastModified");
	    Field jlcType = c.getDeclaredField("jlc$ClassType");
	    String t = (String)jlcType.get(null);
	    TypeEncoder te = new TypeEncoder(ts);
	    return new TypeDumper(name,
				  te.decode(t, name),
				  (String)jlcVersion.get(null),
				  (Long)jlcTimestamp.get(null));
	} catch (IllegalAccessException exn) {
	    throw new SecurityException("illegal access: "+exn.getMessage());
	}
    }

    public void dump(CodeWriter w) {
	Map<Object, Object> cache = CollectionFactory.newHashMap();
	cache.put(theType, theType);
	w.write("Type "+rawName+ " {");
	w.allowBreak(2);
	w.begin(0);
	w.write("Compiled with polyglot version "+compilerVersion+".  ");
	w.allowBreak(0);
	w.write("Last modified: "+timestamp.toString()+".  ");
	w.allowBreak(0);
	w.write(theType.toString());
	w.allowBreak(4);
	w.write("<"+
		theType.getClass().toString()+">");
	w.allowBreak(0);
	dumpObject(w, theType, cache);
	w.allowBreak(0);
	w.end();
	w.allowBreak(0);
	w.write("}");
	w.newline(0);
    }

    protected void dumpObject(CodeWriter w, Object obj, Map<Object, Object> cache) {
	w.write(" fields {");
	w.allowBreak(2);
	w.begin(0);
	try {
	    Field[] declaredFields =
		obj.getClass().getDeclaredFields();
	    java.lang.reflect.AccessibleObject.setAccessible(declaredFields,
							    true);
	    for (int i = 0; i < declaredFields.length; i++) {
		if (Modifier.isStatic(declaredFields[i].getModifiers()))
		    continue;
		w.begin(4);
		w.write(declaredFields[i].getName()+": ");
		w.allowBreak(0);
		try {
		    Object o = declaredFields[i].get(obj);
		    if (o != null) {
			Class<? extends Object> rtType = o.getClass();
			w.write("<"+rtType.toString()+">:");
			w.allowBreak(0);
			w.write(o.toString());
			w.allowBreak(4);
			if (!Object.class.equals(rtType) &&
			    !dontDump(rtType) &&
			    !rtType.isArray() &&
			    !(cache.containsKey(o) &&
			      cache.get(o) == o)) {
			    cache.put(o, o);
			    dumpObject(w, o, cache);
			}
		    } else {
			w.write("null");
		    }
		} catch (IllegalAccessException exn) {
		    w.write("##["+exn.getMessage()+"]");
		}
		w.end();
		w.allowBreak(0);
	    }
	} catch (SecurityException exn) {
	} finally {
	    w.end();
	    w.allowBreak(0);
	    w.write("}");
	}
    }

    static boolean dontDump(Class<?> c) {
	return dontExpand.contains(c);
    }

}

