/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.emitter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.visit.Translator;
import x10.visit.X10PrettyPrinterVisitor;

/**
 * Expand a given template in a loop with the given set of arguments.
 * For the loop body, pass in an array of Lists of identical length
 * (each list representing all instances of a given argument),
 * which will be translated into array-length repetitions of the
 * loop body template.
 * If the template has only one argument, a single list can be used.
 */
public class Loop extends Expander {
	private final String id;
	private final String regex;
	private final List<?>[] lists;
	private final int N;
	
    public Loop(Emitter er, String id, String regex, List<?> arg) {
        this(er, id, regex, new List[] { arg });
    }
    public Loop(Emitter er, String id, String regex, List<?> arg1, List<?> arg2) {
        this(er, id, regex, new List[] { arg1, arg2 });
    }
    public Loop(Emitter er, String id, String regex, List<?> arg1, List<?> arg2, List<?> arg3) {
        this(er, id, regex, new List[] { arg1, arg2, arg3 });
    }
    public Loop(Emitter er, String id, String regex, List<?> arg1, List<?> arg2, List<?> arg3, List<?> arg4) {
        this(er, id, regex, new List[] { arg1, arg2, arg3, arg4 });
    }
	public Loop(Emitter er, String id, String regex, List<?>[] components) {
		super(er);
	
		this.id = id;
		this.regex = regex;
		assert(regex != null);
		this.lists = components;
		// Make sure we have at least one parameter
		assert(lists.length > 0);
		int n = -1;
		int i = 0;
		for (; i < lists.length && n == -1; i++)
			n = lists[i].size();
		// Make sure the lists are all of the same size or circular
		for (; i < lists.length; i++)
			assert(lists[i].size() == n || lists[i].size() == -1);
		this.N = n;
	}
	
    @Override
	public void expand(Translator tr) {
		er.w.write("/* Loop: { */");
//		Object[] args = new Object[lists.length];
        Map<String,Object> components = new HashMap<String,Object>();
		Iterator<?>[] iters = new Iterator[lists.length];
		// Parallel iterators over all argument lists
		for (int j = 0; j < lists.length; j++)
			iters[j] = lists[j].iterator();
		for (int i = 0; i < N; i++) {
		    components.clear();
			for (int j = 0; j < lists.length; j++) {
			    Object component = iters[j].next();
//				args[j] = component;
				components.put(String.valueOf(j), component);
			}
//            er.dumpRegex(id, args, tr, regex);
			er.dumpRegex(id, components, tr, regex);
		}
		er.w.write("/* } */");
	}
    
    @Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Loop ");
		for (int i=0; i < lists.length; ++i) {
			sb.append(er.convertToString(lists[i]));
			if (i+1 < lists.length) 
				sb.append(" ");
		}
		return sb.toString();
	}
}
