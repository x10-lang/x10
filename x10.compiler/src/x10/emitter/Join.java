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

package x10.emitter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.visit.Translator;

/**
 * Join a given list of arguments with a given delimiter.
 * Do not join a circular list.
 */
public class Join extends Expander {

	private final String delimiter;
	private final List<? extends Object> args;
	
	public Join(Emitter er, String delimiter, Object a) {
		this(er, delimiter, Collections.singletonList(a));
	}
	
	public Join(Emitter er, String delimiter, Object... objs) {
	        this(er, delimiter, Arrays.asList(objs));
	}
	
	public Join(Emitter er, String delimiter, List<? extends Object> args) {
		super(er);
		this.delimiter = delimiter;
		this.args = args;
	}
	
    @Override
    public void expand(Translator tr) {
//		er.w.write("/* Join: { */");
		for (Iterator<? extends Object> i = args.iterator(); i.hasNext(); ) {
			er.prettyPrint(i.next(), tr);
			if (i.hasNext())
				er.prettyPrint(delimiter, tr);
		}
//		er.w.write("/* } */");
	}
    
    @Override
    public String toString() {
    	return "Join " + er.convertToString(args);
    }
    
}