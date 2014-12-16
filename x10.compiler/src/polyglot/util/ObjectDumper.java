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

package polyglot.util;

import polyglot.util.*;
import polyglot.visit.NodeVisitor;
import polyglot.ast.Node;
import polyglot.types.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import x10.util.CollectionFactory;

public class ObjectDumper {
    CodeWriter w;
    int modifiersMask;
    
    public ObjectDumper(CodeWriter w) {
        this(w, Modifier.TRANSIENT | Modifier.STATIC);
    }

    public ObjectDumper(CodeWriter w, int modifiersMask) {
        this.w = w;
        this.modifiersMask = modifiersMask;
    }
    
    public void dump(Object o) {
	Set<Object> cache = CollectionFactory.newHashSet();
	w.write("(");
        dumpObject(o, cache);
	w.write(")");
	w.newline(0);
        try {
            w.flush();
        }
        catch (IOException e) {}
    }
  
    protected void dumpObject(Object obj, Set<Object> cache) {
        if (obj == null) {
            w.write("null");
            return;
        }
        
        w.write(StringUtil.getShortNameComponent(obj.getClass().getName()));

//        w.allowBreak(0, " ");
//        w.write(obj.toString());
        
        if (cache.contains(obj)) {
            return;
        }
        cache.add(obj);

        w.allowBreak(1, " ");
        w.begin(0);

	try {
	    Field[] fields = obj.getClass().getDeclaredFields();
	    java.lang.reflect.AccessibleObject.setAccessible(fields, true);
	    for (int i = 0; i < fields.length; i++) {
		Field field = fields[i];
                if ((field.getModifiers() & modifiersMask) != 0)
                    continue;
		w.write("(");
                w.write(field.getName());
                w.allowBreak(1, " ");
		try {
		    Object o = field.get(obj);
		    dumpObject(o, cache);
		}
                catch (IllegalAccessException exn) {
		    w.write("##["+exn.getMessage()+"]");
		}
                w.write(")");
                w.newline(0);
	    }
	}
        catch (SecurityException exn) {
	}

        w.end();
    }
}

