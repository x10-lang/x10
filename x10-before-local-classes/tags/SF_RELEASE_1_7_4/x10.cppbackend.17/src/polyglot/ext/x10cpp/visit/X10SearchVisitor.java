/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 7, 2004
 */
package polyglot.ext.x10cpp.visit;

import java.util.ArrayList;

import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

/**
 * @author V. Krishna Nandivada
 * @author Igor Peshansky
 */
public class X10SearchVisitor extends NodeVisitor {
	public static boolean debug = false;
	private ArrayList searchNames;
	private boolean found = false;
	private ArrayList results;
	private final boolean oneMatch;

	public X10SearchVisitor(Class z, boolean oneMatch) {
		this.oneMatch = oneMatch;
		reset(z);
	}

	public X10SearchVisitor(Class z, Class y, boolean oneMatch) {
		this(z, oneMatch);
		addNodeType(y);
	}

	public X10SearchVisitor(Class z, Class y, Class x, boolean oneMatch) {
		this(z, y, oneMatch);
		addNodeType(x);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, boolean oneMatch) {
		this(z, y, x, oneMatch);
		addNodeType(w);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, Class v, boolean oneMatch) {
		this(z, y, x, w, oneMatch);
		addNodeType(v);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, Class v, Class u, boolean oneMatch) {
		this(z, y, x, w, v, oneMatch);
		addNodeType(u);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, Class v, Class u, Class t, boolean oneMatch) {
		this(z, y, x, w, v, u, oneMatch);
		addNodeType(t);
	}

	public X10SearchVisitor(Class z) {
		this(z, false);
	}

	public X10SearchVisitor(Class z, Class y) {
		this(z, y, false);
	}

	public X10SearchVisitor(Class z, Class y, Class x) {
		this(z, y, x, false);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w) {
		this(z, y, x, w, false);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, Class v) {
		this(z, y, x, w, v, false);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, Class v, Class u) {
		this(z, y, x, w, v, u, false);
	}

	public X10SearchVisitor(Class z, Class y, Class x, Class w, Class v, Class u, Class t) {
		this(z, y, x, w, v, u, t, false);
	}

	public void reset(Class x) {
		searchNames = new ArrayList();
		searchNames.add(x);
		found = false;
		results = new ArrayList();
	}

	public void reset(Class z, Class y) {
		reset(z);
		addNodeType(y);
	}

	public void reset(Class z, Class y, Class x) {
		reset(z, y);
		addNodeType(x);
	}

	public void reset(Class z, Class y, Class x, Class w) {
		reset(z, y, x);
		addNodeType(w);
	}

	public void reset(Class z, Class y, Class x, Class w, Class v) {
		reset(z, y, x, w);
		addNodeType(v);
	}

	public void reset(Class z, Class y, Class x, Class w, Class v, Class u) {
		reset(z, y, x, w, v);
		addNodeType(u);
	}

	public void reset(Class z, Class y, Class x, Class w, Class v, Class u, Class t) {
		reset(z, y, x, w, v, u);
		addNodeType(t);
	}

	public void addNodeType(Class x) {
		searchNames.add(x);
	}

	public boolean found() {
		return found;
	}

	public Node getMatch() {
		assert(oneMatch);
		assert(found);
		return (Node) results.get(0);
	}

	public ArrayList getMatches() {
		assert(found);
		return results;
	}

	private boolean isMatch(Class type) {
		for (int i = 0; i < searchNames.size(); i++)
			if (((Class) searchNames.get(i)).isAssignableFrom(type))
				return true;
		return false;
	}

	private void addMatch(Node n) {
		found = true;
		results.add(n);
	}

	private void checkNode(Node n) {
		Class type = n.getClass();
		if (debug) {
			System.out.println("String = " + type);
			System.out.println("Context = " + searchNames);
		}
		if (oneMatch && found)
			return;
		if (isMatch(type))
			addMatch(n);
		return;
	}

	public Node override(Node parent, Node child) {
		checkNode(child);
		return null;
	}
} // end of X10SearchVisitor

