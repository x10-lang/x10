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

package x10cpp.visit;

import java.util.ArrayList;

import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

/**
 * @author V. Krishna Nandivada
 * @author Igor Peshansky
 */
public class X10SearchVisitor<T extends Node> extends NodeVisitor {
	public static boolean debug = false;
	private ArrayList<Class<? extends T>> searchNames;
	private boolean found = false;
	private ArrayList<T> results;
	private final boolean oneMatch;

	public X10SearchVisitor(Class<? extends T> z, boolean oneMatch) {
		this.oneMatch = oneMatch;
		reset(z);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, boolean oneMatch) {
		this(z, oneMatch);
		addNodeType(y);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, boolean oneMatch) {
		this(z, y, oneMatch);
		addNodeType(x);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, boolean oneMatch) {
		this(z, y, x, oneMatch);
		addNodeType(w);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, boolean oneMatch) {
		this(z, y, x, w, oneMatch);
		addNodeType(v);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, Class<? extends T> u, boolean oneMatch) {
		this(z, y, x, w, v, oneMatch);
		addNodeType(u);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, Class<? extends T> u, Class<? extends T> t, boolean oneMatch) {
		this(z, y, x, w, v, u, oneMatch);
		addNodeType(t);
	}

	public X10SearchVisitor(Class<? extends T> z) {
		this(z, false);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y) {
		this(z, y, false);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x) {
		this(z, y, x, false);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w) {
		this(z, y, x, w, false);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v) {
		this(z, y, x, w, v, false);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, Class<? extends T> u) {
		this(z, y, x, w, v, u, false);
	}

	public X10SearchVisitor(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, Class<? extends T> u, Class<? extends T> t) {
		this(z, y, x, w, v, u, t, false);
	}

	public void reset(Class<? extends T> x) {
		searchNames = new ArrayList<Class<? extends T>>();
		searchNames.add(x);
		found = false;
		results = new ArrayList<T>();
	}

	public void reset(Class<? extends T> z, Class<? extends T> y) {
		reset(z);
		addNodeType(y);
	}

	public void reset(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x) {
		reset(z, y);
		addNodeType(x);
	}

	public void reset(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w) {
		reset(z, y, x);
		addNodeType(w);
	}

	public void reset(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v) {
		reset(z, y, x, w);
		addNodeType(v);
	}

	public void reset(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, Class<? extends T> u) {
		reset(z, y, x, w, v);
		addNodeType(u);
	}

	public void reset(Class<? extends T> z, Class<? extends T> y, Class<? extends T> x, Class<? extends T> w, Class<? extends T> v, Class<? extends T> u, Class<? extends T> t) {
		reset(z, y, x, w, v, u);
		addNodeType(t);
	}

	public void addNodeType(Class<? extends T> x) {
		searchNames.add(x);
	}

	public boolean found() {
		return found;
	}

	public T getMatch() {
		assert(oneMatch);
		assert(found);
		return results.get(0);
	}

	public ArrayList<T> getMatches() {
		assert(found);
		return results;
	}

	private boolean isMatch(Class<? extends Node> type) {
		for (Class<? extends T> c : searchNames)
		    if (c.isAssignableFrom(type))
		        return true;
		return false;
	}

	private void addMatch(T n) {
		found = true;
		results.add(n);
	}

	@SuppressWarnings("unchecked") // Casting to a generic type argument
    private void checkNode(Node n) {
		Class<? extends Node> type = n.getClass();
		if (debug) {
			System.out.println("String = " + type);
			System.out.println("Context = " + searchNames);
		}
		if (isMatch(type))
			addMatch((T)n);
		return;
	}

    @Override
	public Node override(Node parent, Node child) {
		if (oneMatch && found)
		    return child;
		checkNode(child);
		return null;
	}
} // end of X10SearchVisitor

