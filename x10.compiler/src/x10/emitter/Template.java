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

import java.util.ArrayList;
import java.util.Map;

import polyglot.visit.Translator;

// not used
/**
 * Expand a given template with the given set of arguments.
 * Equivalent to a Loop with an array of singleton lists.
 * If the template has zero, one, two, or three arguments, the
 * arguments can be passed in directly to the constructor.
 */
@Deprecated
public class Template extends Expander {
	private final String id;
	private final String regex;
	private final Map<String,Object> components;

	private Template(Emitter er, String id, String regex, Map<String,Object> components) {
		super(er);
		
        assert id != null;
		this.id = id;
		this.regex = regex;
		assert regex != null;
		this.components = components;
	}

	public static Template createTemplateFromRegex(Emitter er, String id, String regex, Map<String,Object> components) {
	    return new Template(er, id, regex, components);
	}

	@Override
	public void expand(Translator tr) {
		er.dumpRegex(id, components, tr, regex);
	}
    
    @Override
	public String toString() {
    	ArrayList<Object> l = new ArrayList<Object>();
    	for (Map.Entry<String, Object> e : components.entrySet()) {
    		l.add(e);
    	}
		return id + " " + Emitter.convertToString(l.toArray());
	}
	
}